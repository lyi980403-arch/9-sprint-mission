package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Profile("!test")
@Component
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

  private static final String PREFIX_QUEUE   = "jwt:user:%s:queue";
  private static final String PREFIX_ACCESS  = "jwt:access:%s";
  private static final String PREFIX_REFRESH = "jwt:refresh:%s";
  private static final String PREFIX_LOCK    = "jwt:rotate-lock:%s";

  // 토큰 인덱스 TTL — 리프레시 토큰 최대 수명보다 넉넉하게
  private static final long TOKEN_INDEX_TTL_SECONDS = 60 * 60 * 24 * 8L; // 8일

  private static final long LOCK_WAIT_SECONDS  = 3L;
  private static final long LOCK_LEASE_SECONDS = 5L;

  private final RedissonClient redissonClient;
  private final JwtTokenProvider jwtTokenProvider;
  private final int maxActiveJwtCount;

  // ── registerJwtInformation ────────────────────────────────

  @CacheEvict(value = "users", key = "'all'")
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();
    RList<JwtInformation> queue = redissonClient.getList(queueKey(userId));

    // 최대 개수 초과 시 가장 오래된 항목 제거
    while (queue.size() >= maxActiveJwtCount) {
      JwtInformation old = queue.remove(0);
      if (old != null) {
        removeTokenIndex(old.getAccessToken(), old.getRefreshToken());
        log.debug("[RedisJwtRegistry] 오래된 토큰 제거: userId={}", userId);
      }
    }

    queue.add(jwtInformation);
    addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
    log.debug("[RedisJwtRegistry] 토큰 등록: userId={}", userId);
  }

  // ── invalidateJwtInformationByUserId ─────────────────────

  @CacheEvict(value = "users", key = "'all'")
  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    RList<JwtInformation> queue = redissonClient.getList(queueKey(userId));
    for (JwtInformation info : queue) {
      removeTokenIndex(info.getAccessToken(), info.getRefreshToken());
    }
    queue.delete();
    log.debug("[RedisJwtRegistry] 사용자 토큰 전체 무효화: userId={}", userId);
  }

  // ── hasActive* ────────────────────────────────────────────

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return redissonClient.getList(queueKey(userId)).isExists();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return redissonClient.getBucket(accessKey(accessToken)).isExists();
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return redissonClient.getBucket(refreshKey(refreshToken)).isExists();
  }

  // ── rotateJwtInformation (분산락) ─────────────────────────

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    RLock lock = redissonClient.getLock(lockKey(refreshToken));
    try {
      boolean acquired = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
      if (!acquired) {
        log.warn("[RedisJwtRegistry] 분산락 획득 실패 — 동시 rotate 요청 감지");
        return;
      }

      UUID userId = newJwtInformation.getUserDto().id();
      RList<JwtInformation> queue = redissonClient.getList(queueKey(userId));

      for (JwtInformation info : queue) {
        if (info.getRefreshToken().equals(refreshToken)) {
          removeTokenIndex(info.getAccessToken(), info.getRefreshToken());
          info.rotate(
              newJwtInformation.getAccessToken(),
              newJwtInformation.getRefreshToken()
          );
          addTokenIndex(
              newJwtInformation.getAccessToken(),
              newJwtInformation.getRefreshToken()
          );
          log.debug("[RedisJwtRegistry] 토큰 rotate 완료: userId={}", userId);
          break;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("[RedisJwtRegistry] 분산락 대기 중 인터럽트", e);
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  // ── clearExpiredJwtInformation ────────────────────────────

  @Override
  public void clearExpiredJwtInformation() {
    Iterable<String> keys = redissonClient.getKeys().getKeysByPattern("jwt:user:*:queue");
    for (String key : keys) {
      RList<JwtInformation> queue = redissonClient.getList(key);
      List<JwtInformation> toRemove = new ArrayList<>();

      for (JwtInformation info : queue) {
        boolean expired =
            !jwtTokenProvider.validateAccessToken(info.getAccessToken()) ||
                !jwtTokenProvider.validateRefreshToken(info.getRefreshToken());
        if (expired) {
          removeTokenIndex(info.getAccessToken(), info.getRefreshToken());
          toRemove.add(info);
        }
      }

      if (!toRemove.isEmpty()) {
        queue.removeAll(toRemove);
        log.debug("[RedisJwtRegistry] 만료 토큰 {}건 정리: key={}", toRemove.size(), key);
      }
      if (queue.isEmpty()) {
        queue.delete();
      }
    }
  }

  // ── private helpers ───────────────────────────────────────

  private void addTokenIndex(String accessToken, String refreshToken) {
    redissonClient.<String>getBucket(accessKey(accessToken))
        .set("1", TOKEN_INDEX_TTL_SECONDS, TimeUnit.SECONDS);
    redissonClient.<String>getBucket(refreshKey(refreshToken))
        .set("1", TOKEN_INDEX_TTL_SECONDS, TimeUnit.SECONDS);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    redissonClient.getBucket(accessKey(accessToken)).delete();
    redissonClient.getBucket(refreshKey(refreshToken)).delete();
  }

  private String queueKey(UUID userId)    { return String.format(PREFIX_QUEUE,   userId); }
  private String accessKey(String token)  { return String.format(PREFIX_ACCESS,  token);  }
  private String refreshKey(String token) { return String.format(PREFIX_REFRESH, token);  }
  private String lockKey(String token)    { return String.format(PREFIX_LOCK,    token);  }
}
