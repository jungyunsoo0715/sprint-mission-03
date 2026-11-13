package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelResponseByUserId(
        UUID id,
        ChannelType channelType,
        String name,
        String description,
        Set<UUID> participantIds,
        Instant lastMessageAt
) {
}
