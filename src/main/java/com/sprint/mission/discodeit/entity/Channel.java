package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    /* Instant는 자바에서 특정 시점(날짜와 시간)을 다루는 클래스입니다.
     *
     */
    private Instant createdAt; // Long -> Instant
    private Instant updatedAt; // Long -> Instant
    //
    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        /* 초 단위로 표현합니다. long을 반환합니다. 타임스탬프(long 타입)를 저장하는 데 쓰일 수 있습니다. */
        // this.createdAt = Instant.now().getEpochSecond();
        /* 나노초 단위로 표현합니다. Instant 객체를 반환합니다. */
        this.createdAt = Instant.now();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /* Getter를 직접 작성하는 대신에, Lombok 라이브러리의 @Getter를 클래스에 붙여 Getter를 생성합니다.
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    */

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
