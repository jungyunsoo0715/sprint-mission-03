package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    /* 생성자를 직접 작성하는 대신에, Lombok 라이브러리의 @RequiredArgsConstructor를 클래스에 붙여 기본 생성자를 생성합니다.
    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    */

    /* DTO 사용 전
    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        return userRepository.save(user);
    }
    */

    // DTO 사용 후
    @Override
    public User create(UserCreateRequest request) {
        // throw: 메서드 내부에서 실제로 예외를 발생시킬 때 사용합니다.
        // throws: 해당 메서드를 실행하는 도중, 특정 예외가 발생할 수 있다는 사실을 호출자에게 알리는 역할을합니다.
        // 메서드 선언부에 사용합니다. e.g. public void readFile() throws IOException { // body }
        // throws는 Unchecked Exception(예외 처리를 컴파일러가 강제하지 않는 예외)일 때는 사용하지 않아도
        // 기능상 차이는 없습니다.
        // Checked Exception(예외 처리 또는 throws 선언을 컴파일러가 강제하는 예외)일 때는 throws를 선언하거나,
        // 메서드 내부에서 try-catch로 처리해여합니다.
        // 이 둘 중 하나를 하지 않으면 컴파일 에러가 발생합니다.
        if (this.isUniqueUsername(request.username())) {
            // Unchecked Exception입니다.
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }
        if (this.isUniqueEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 email 입니다.");
        }
        // Java의 Record는 필드에 접근할 때 필드명을 메서드 처럼 사용합니다.
        User user = new User(request.username(), request.email(), request.password(), request.profileId());

        // UserStatus 생성, 엔티티는 빈으로 등록하지 않습니다.
        // 엔티티는 여러 사용자가 독립적으로 사용할 수 있기 때문에 싱글톤으로 관리되면 안되고,
        // 만약 엔티티가 싱글톤으로 관리된다면,
        // 공유 가변 상태(Shared Mutable State)가 되기 때문에 동시성 문제가 발생할 수 있습니다.
        // 스프링의 빈은 공유 가변 상태를 가져서는 안됩니다. 단 다른 빈을 주입받아 가지고있는 것은 가능한데
        // 주입받는 빈도 상태가 없거나 동시성에 안전하게 설계되어 있다면 문제가 없습니다.
        // final 키워드는 참조 변수의 재할당을 금지할 뿐, 객체 자체를 불변으로 만들지는 않습니다.
        // 일단 이렇게 객체만 생성합니다. 자바에서 도달할 수 없는 구문(Unreachable Statement)은 컴파일 에러를 발생시킵니다.
        // UserStatus userStatus = new UserStatus(request.profileId());

        UserStatus userStatus = new UserStatus(user.getId());
        this.userStatusRepository.save(userStatus);

        BinaryContent binaryContent = new BinaryContent();
        this.binaryContentRepository.save(binaryContent);

        return userRepository.save(user);
    }

    public boolean isUniqueUsername(String username) {
        boolean isUniqueUsername = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));

        return isUniqueUsername;
    }

    public boolean isUniqueEmail(String email) {
        boolean isUniqueEmail = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));

        return isUniqueEmail;
    }

    // 일반적으로 Repository는 Entity를 반환하고, Service에서 Entity를 DTO로 변환합니다.
    /* DTO 사용 전
    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }
    */

    // DTO 사용 후
    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.findUserStatus(user.getId());

        UserResponse response = new UserResponse(
                user.getId(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline()
        );

        return response;
    }

    // DTO 적용 여부와 관계 없이 수정이 불필요합니다.
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /* DTO 사용 전
    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }
    */

    @Override
    public User update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), request.newProfileId());
        return userRepository.save(user);
    }

    /* 관련 도메인(Binary Content, UserStatus) 동반 삭제 구현 전
    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
    */


    // 관련 도메인(Binary Content, UserStatus) 동반 삭제 구현 전
    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        this.userRepository.deleteById(userId);
        this.userStatusRepository.deleteById(userId);
        Optional<BinaryContent> binaryContent = this.findBinaryContent(userId);
        this.binaryContentRepository.deleteById(binaryContent.get().getId());
    }

    public UserStatus findUserStatus(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        return userStatusRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자의 프로필 이미지를을 찾을 수 없습니다."));
    }

    public Optional<BinaryContent> findBinaryContent(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        Optional<User> user = userRepository.findById(userId);
        // user.get()은 Optional 객체 안에 들어 있는 값을 꺼내는 메서드입니다.
        UUID profileId = user.get().getProfileId();

        return binaryContentRepository.findById(profileId);
    }
}
