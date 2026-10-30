package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = new Channel(request);
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(request);
        List<ReadStatus> readStatusList = request.userList().stream()
                .map(u -> new ReadStatus(u.getId(), channel.getId()))
                .toList();

        this.channelRepository.save(channel);
        this.readStatusRepository.saveAll(readStatusList);

        return channel;
    }

    private Instant findLastMessageAt(UUID channelId) {
        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    private ChannelInfo toChannelInfo(Channel channel) {
        if (channel.getType() == ChannelType.PRIVATE) {
            Set<UUID> participantIds = this.readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toSet());

            return new PrivateChannelInfo(
                    channel,
                    findLastMessageAt(channel.getId()),
                    participantIds
            );
        } else {
            return new PublicChannelInfo(
                    channel,
                    findLastMessageAt(channel.getId())
            );
        }
    }

    @Override
    public ChannelInfo find(ChannelFindRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        return toChannelInfo(channel);
    }

    /*
    @Override
    public List<ChannelInfo> findAll() {
        return this.channelRepository.findAll().stream()
                .map(this::toChannelInfo)
                .toList();
    }
     */

    // 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
    // ReadStatus를 사용
    public List<ChannelInfo> findAllByUserId(UUID userId) {
        List<ChannelInfo> availablePrivateChannelsByUserId =
                this.readStatusRepository.findAll().stream()
                        .filter(rs -> rs.getUserId().equals(userId))
                        .map(rs -> this.channelRepository.findById(rs.getChannelId())
                                .orElseThrow(() -> new NoSuchElementException("Channel not found")))
                        .filter(c -> c.getType() == ChannelType.PRIVATE)
                        .map(this::toChannelInfo)
                        .toList();

        List<ChannelInfo> publicChannelsByUserId = this.channelRepository.findAll().stream()
                .filter(c -> c.getType().equals(ChannelType.PUBLIC))
                .map(this::toChannelInfo)
                .toList();

        return Stream.concat(availablePrivateChannelsByUserId.stream(), publicChannelsByUserId.stream()).toList();
    }

    @Override
    public ChannelInfo update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("프라이빗 채널은 수정할 수 없습니다.");
        }
        channel.update(request.newName(), request.newDescription());
        this.channelRepository.save(channel);
        return this.toChannelInfo(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("Channel with id " + channelId + " not found"));

        List<Message> messageList = this.messageRepository.findAll();
        messageList.stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .forEach(m -> this.messageRepository.deleteById(m.getId()));

        List<ReadStatus> readStatusList = this.readStatusRepository.findAll();
        readStatusList.stream()
                .filter(rs -> rs.getChannelId().equals(channel.getId()))
                .forEach(rs -> this.readStatusRepository.deleteById(rs.getId()));

        this.channelRepository.deleteById(channelId);
    }
}
