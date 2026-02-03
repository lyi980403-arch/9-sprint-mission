package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private final UUID id;
    private final String filename;
    private final String contentType;
    private final byte[] bytes;
    private final Instant createdAt;

    public BinaryContent(
            String filename,
            String contentType,
            byte[] bytes
    ) {
        this.id = UUID.randomUUID();
        this.filename = filename;
        this.contentType = contentType;
        this.bytes = bytes;
        this.createdAt = Instant.now();
    }
}

