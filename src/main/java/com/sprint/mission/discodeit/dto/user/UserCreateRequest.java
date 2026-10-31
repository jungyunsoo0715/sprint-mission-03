package com.sprint.mission.discodeit.dto.user;

// @Nullable가지고 있는 패키지 중에 이걸 선택합니다
// Spring Boot 3.x: import org.springframework.lang.Nullable;
// Spring Boot 4.x: import org.jspecify.annotations.Nullable;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import org.jspecify.annotations.Nullable;

// @Getter // Lombok의 @Getter는 클래스, 열거형 또는 필드 타입에서만 제공됩니다.
public record UserCreateRequest(
        String username,
        String email,
        String password,
        // 프로필 이미지는 User객체 생성시 선택 사항입니다, 따라서 null 이 들어갈 수 있습니다.
        // 다만 굳이, @Nullable로 null 값이 들어갈 수 있음을 명시할 필요는 없습니다.
        // @Nullable UUID profileId
        @Nullable BinaryContentCreateRequest profile
) {
}
