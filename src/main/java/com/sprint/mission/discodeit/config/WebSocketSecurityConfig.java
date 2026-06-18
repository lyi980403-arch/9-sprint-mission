package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.Message;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // Spring Security 필터보다 먼저 실행
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    AuthorizationManager<Message<?>> authorizationManager = buildAuthorizationManager();

    registration.interceptors(
        // 1. JWT 인증: CONNECT 시 accessor에 Authentication 저장
        jwtAuthenticationChannelInterceptor,

        // 2. SecurityContext 전파: accessor의 user → SecurityContextHolder
        new SecurityContextChannelInterceptor(),

        // 3. 인가: 구독/발행 경로별 권한 검사
        new AuthorizationChannelInterceptor(authorizationManager)
    );
  }

  /**
   * 메시지 목적지(destination)별 인가 정책 정의
   */
  private AuthorizationManager<Message<?>> buildAuthorizationManager() {
    MessageMatcherDelegatingAuthorizationManager.Builder messages =
        MessageMatcherDelegatingAuthorizationManager.builder();

    messages
        // CONNECT/DISCONNECT는 인증 후 처리되므로 허용
        .nullDestMatcher().authenticated()

        // 채널 구독: 인증된 사용자만 허용
        .simpSubscribeDestMatchers("/topic/**").authenticated()
        .simpSubscribeDestMatchers("/queue/**").authenticated()
        .simpSubscribeDestMatchers("/user/**").authenticated()

        // 메시지 발행: 인증된 사용자만 허용
        .simpDestMatchers("/app/**").authenticated()

        // 그 외 모든 메시지 거부
        .anyMessage().denyAll();

    return messages.build();
  }
}
