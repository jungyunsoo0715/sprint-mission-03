package com.sprint.mission.discodeit.dto.authentication;

public record LoginRequest(
        String username,
        String password
) {
}
