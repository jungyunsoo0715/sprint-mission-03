package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    /*
    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author not found with id " + authorId);
        }

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }
     */

    // 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
    // DTO를 활용해 파라미터를 그룹화합니다.
    @Override
    public Message create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        // null보다 List.of()를 사용, 빈 리스트를 사용하면 나중에 첨부파일 목록을 순회할 때 불필요한 null 검사를 줄일 수 있다.
        // List.of()는 수정이 불가능하기 때문에 new ArrayList<>(); 를 사용하는 것이 좋다.
        List<UUID> newAttachmentIds = new ArrayList<>();

        if (request.attachments() != null) {
            List<BinaryContent> attachments = new ArrayList<>(request.attachments());
            newAttachmentIds = attachments.stream()
                    .map(a -> this.binaryContentRepository
                            .save(new BinaryContent(
                                    a.getFileName(),
                                    a.getContentType(),
                                    a.getBytes()
                            ))
                            .getId())
                    .toList();
        }

        Message message = new Message(request.content(), request.channelId(), request.authorId(), newAttachmentIds);
        return messageRepository.save(message);
    }



    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    /*
    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }
    */

    // 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findallByChannelId
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    /*
    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        return messageRepository.save(message);
    }
    */

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        String newContent; // null로 초기화 할 필요가없습니다. 조건문에서 값을 할당하기 때문입니다.
        // 지역변수는 사용하기 전에 반드시 초기화되어 있어야 합니다.

        // request.newContent() != null || !request.newContent().isEmpty() 이면
        // newContent가 null이면 → 두 번째 조건을 검사하면서 NullPointerException 발생,
        // || 연산자는 왼쪽이 false이면 오른쪽을 평가하는데 이 때 NPE가 발생합니다.
        // newContent가 빈 문자열 ""이면 → 첫 번째 조건이 true이므로 빈 문자열도 허용됨
        // &&를 사용하면 → null이 아니면서 빈 문자열도 아닐 때만 변경
        if (request.newContent() != null && !request.newContent().isEmpty()) {
            newContent = request.newContent();
        } else {
            // MessageUpdateRequest DTO에 문자열이 없을 때, 원래 콘텐츠를 유지합니다.
            newContent = message.getContent();
        }

        List<BinaryContent> newBinaryContents = new ArrayList<BinaryContent>();
        // List<UUID> attachmentIds = new ArrayList<>(); 난 이것을 왜쓴것인가??

        if (request.newBinaryContents() != null && !request.newBinaryContents().isEmpty()) {
            request.newBinaryContents().forEach(b-> {
                BinaryContent saved = this.binaryContentRepository.save(
                        new BinaryContent(b.getFileName(), b.getContentType(), b.getBytes()));
                newBinaryContents.add(saved);
                }
            );
        }

        message.update(newContent, newBinaryContents);
        this.messageRepository.save(message);

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }

        // [ ] 관련된 도메인도 같이 삭제합니다. 첨부파일(BinaryContent)
        // Message를 삭제할 때, 그 메시지의 attachmentId에 해당하는 모든 binaryContent를 삭제한다.

        // 1. 메시지 객체 가져오기.
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Message with id " + messageId + " not found"));

        // 2. 메시지의 attachmentIds 가져오기.
        List<UUID> attachmentIds = message.getAttachmentIds();

        // 3. attachementIds 에 있는 모든 id값을 가지고있는 binaryContent 삭제하기.
        this.binaryContentRepository.findAll().stream()
                .filter(b -> attachmentIds.contains(b.getId()))
                .forEach(b -> this.binaryContentRepository.deleteById(b.getId()));

        messageRepository.deleteById(messageId);
    }
}
