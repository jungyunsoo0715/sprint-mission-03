package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record MessageUpdateRequest(
        @Nullable String newContent,
        @Nullable List<BinaryContent> newBinaryContents
) { }
