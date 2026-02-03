package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String userName,
        String email,
        UUID profileId,
        boolean isOnline,
        Instant createAt
) {
}

