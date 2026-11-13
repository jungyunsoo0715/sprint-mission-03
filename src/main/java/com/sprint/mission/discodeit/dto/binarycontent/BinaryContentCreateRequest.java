package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.NonNull;

public record BinaryContentCreateRequest(
        @NotBlank
        String fileName,
        @NonNull
        Long size,
        @NotBlank
        String contentType,
        @NotEmpty
        byte[] bytes
) {
}
