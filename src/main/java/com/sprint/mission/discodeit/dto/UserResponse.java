package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID profileId,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        // String password // password는 제외합니다.
        boolean isOnline
) {
}
