package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record PublicChannelCreateRequest(
        @NotBlank
        String name,

        @Nullable
        String description
) {
}
