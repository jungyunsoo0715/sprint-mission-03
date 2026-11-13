package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusListResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readstatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponse> createReadStatus(
            @Valid @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusResponse readStatusResponse =
                this.readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponse);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
            @PathVariable("readStatusId") UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusResponse readStatusResponse =
                this.readStatusService.update(request);

        return ResponseEntity.ok(readStatusResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ReadStatusListResponse> findReadStatusesByUserId(
            @PathVariable("userId") UUID userId
    ) {
        ReadStatusListResponse readStatusListResponse =
                this.readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(readStatusListResponse);
    }
}

