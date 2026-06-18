package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.RoleUpdatedEvent;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("kafka")
@Component
@RequiredArgsConstructor
public class KafkaSseBroadcastListener {

  private final SseService sseService;
  private final ObjectMapper objectMapper;

  /**
   * 메시지 생성 이벤트 수신 → 해당 채널 구독자에게 SSE 전송
   *
   * groupId를 인스턴스별 고유값으로 설정해 fan-out을 구현합니다.
   * (application.yml: spring.kafka.consumer.group-id = ...-sse-${random.uuid})
   */
  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "${spring.kafka.consumer.group-id}"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.debug("[KafkaSseBroadcastListener] MessageCreatedEvent 수신: channelId={}",
          event.getData().channelId());

      // NotificationRequiredTopicListener가 알림(DB 저장)을 담당하고,
      // 이 리스너는 SSE 실시간 전송만 담당합니다.
      sseService.send(
          Set.of(event.getData().author().id()),
          "MessageCreatedEvent",
          event.getData()
      );
    } catch (JsonProcessingException e) {
      log.error("[KafkaSseBroadcastListener] MessageCreatedEvent 역직렬화 실패", e);
    }
  }

  /**
   * 역할 변경 이벤트 수신 → 대상 사용자에게 SSE 전송
   */
  @KafkaListener(
      topics = "discodeit.RoleUpdatedEvent",
      groupId = "${spring.kafka.consumer.group-id}"
  )
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      log.debug("[KafkaSseBroadcastListener] RoleUpdatedEvent 수신: userId={}", event.getUserId());

      sseService.send(
          Set.of(event.getUserId()),
          "RoleUpdatedEvent",
          event
      );
    } catch (JsonProcessingException e) {
      log.error("[KafkaSseBroadcastListener] RoleUpdatedEvent 역직렬화 실패", e);
    }
  }
}
