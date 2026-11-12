package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelResponse> createChannel(
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelResponse channelResponse = this.channelService.createChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @PatchMapping("/{channel-id}")
    public ResponseEntity<ChannelResponse> updatePublicChannel(
            @PathVariable("channel-id") UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelResponse channelResponse = this.channelService.update(channelId, request);

        return ResponseEntity.ok(channelResponse);
    }

    @DeleteMapping("/{channel-id}")
    public ResponseEntity<ChannelResponse> deleteChannel(
            @PathVariable("channel-id") UUID channelId
    ) {
        this.channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<ChannelListResponse> getVisibleChannels(
            @PathVariable("user-id") UUID userId
    ) {
        List<ChannelResponse> channelResponseList =
                this.channelService.findAllByUserId(userId);
        ChannelListResponse channelListResponseDto =
                new ChannelListResponse(channelResponseList);

        return ResponseEntity.ok(channelListResponseDto);
    }

}
