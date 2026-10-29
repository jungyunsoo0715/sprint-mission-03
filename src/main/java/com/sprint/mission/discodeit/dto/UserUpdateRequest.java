package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
        // UUID userId, // userId는 포함하지 않는다, update()의 인자로 따로 간다.
        String newUsername,
        String newEmail,
        String newPassword,
        UUID newProfileId
) { }
