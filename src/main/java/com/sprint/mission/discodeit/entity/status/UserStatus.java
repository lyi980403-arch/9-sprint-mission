package com.sprint.mission.discodeit.entity.status;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {

    private final UUID id;
    private final UUID userId;
    private Instant lastActiveAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = this.lastActiveAt;
    }

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }
}

