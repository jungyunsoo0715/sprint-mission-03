package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // 인스턴스 필드는 자동으로 null로 초기화됩니다.
    private UUID id;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    */

    /* 프로필 이미지 미적용 전
    public void update(String newUsername, String newEmail, String newPassword) {
        // 조건 플래그(Condition Flag) 또는 상태 플래그(Boolean Flag) 하고 합니다.
        // 특정 조건의 충족 여부나 상태를 기록하기위해 사용합니다.
        // 이를 바탕으로 후속 작업의 실행 여부를 결정합니다.
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
     */

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        // 조건 플래그(Condition Flag) 또는 상태 플래그(Boolean Flag) 하고 합니다.
        // 특정 조건의 충족 여부나 상태를 기록하기위해 사용합니다.
        // 이를 바탕으로 후속 작업의 실행 여부를 결정합니다.
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
