package com.sprint.mission.discodeit.entity.status;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateLastReadAt(Instant time) {
        this.lastReadAt = time;
        this.updatedAt = Instant.now();
    }
}

