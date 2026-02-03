package com.sprint.mission.discodeit.dto.user;

public record BinaryContentCreateRequest(
        String filename,
        String contentType,
        byte[] bytes
) {
}
