package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

public record ChannelCreateRequest(
        @NonNull
        ChannelType type,
        List<UUID> userList,
        String name,
        String description
) {
}
