package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;

import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request);

    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

    // ChannelResponse find(ChannelFindRequest request);

    ChannelListResponse findAllByUserId(UUID userId);

    ChannelResponse update(UUID channelId, ChannelUpdateRequest request);

    void delete(UUID channelId);
}
