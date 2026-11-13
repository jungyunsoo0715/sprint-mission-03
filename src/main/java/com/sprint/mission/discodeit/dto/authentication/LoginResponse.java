package com.sprint.mission.discodeit.dto.authentication;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        String userName,
        String email,
        String password,
        UUID profileId
) {
}
