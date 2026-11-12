package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createChannel(ChannelCreateRequest request);

    ChannelResponse find(ChannelFindRequest request);

    ChannelListResponse findAllByUserId(UUID userId);

    ChannelResponse update(UUID channelId, ChannelUpdateRequest request);

    void delete(UUID channelId);
}
