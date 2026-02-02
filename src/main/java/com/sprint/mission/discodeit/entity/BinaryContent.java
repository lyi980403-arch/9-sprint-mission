package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private final UUID id;
    private final String filename;
    private final String contentType;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(
            UUID id,
            String filename,
            String contentType,
            byte[] data,
            Instant createdAt
    ) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = createdAt;
    }
}

