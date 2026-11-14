package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageResponse messageResponse =
                this.messageService.create(request, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    /*
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestPart("request") MessageUpdateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageResponse messageResponse = this.messageService.update(
                messageId, request, attachments);

        return ResponseEntity.ok(messageResponse);
    }
     */

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageResponse messageResponse = this.messageService.update(
                messageId, request, null);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> findMessage(
            @PathVariable("messageId") UUID messageId
    ) {
        MessageResponse messageResponse = this.messageService.find(messageId);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping
    public ResponseEntity<MessageListResponse> findAllMessage(
            @RequestParam("channelId") UUID channelId
    ) {
        MessageListResponse messageResponseList =
                this.messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messageResponseList);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("messageId") UUID messageId
    ) {
        this.messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }
}
