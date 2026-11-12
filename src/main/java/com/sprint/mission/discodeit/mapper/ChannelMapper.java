package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Component
public class ChannelMapper {
    public ChannelResponse toChannelResponse(
            Channel channel,
            Instant lastMessageAt,
            @Nullable Set<UUID> participantIds
    ) {
        if (channel.getType() == ChannelType.PRIVATE) {
            return new PrivateChannelResponse(
                    channel,
                    lastMessageAt,
                    participantIds
            );
        } else {
            return new PublicChannelResponse(
                    channel,
                    lastMessageAt
            );
        }
    }
}
