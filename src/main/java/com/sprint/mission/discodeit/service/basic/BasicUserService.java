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

    @Override
    public User create(UserCreateRequest request) {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        UUID newProfileId = null;

        if (request.profile() != null) {
            var profile = request.profile();
            newProfileId = binaryContentRepository.save(
                    new BinaryContent(
                            profile.fileName(), profile.contentType(), profile.bytes()
                    )
            ).getId();
        }

        User user = new User(request.username(), request.email(), request.password(), newProfileId);

        UserStatus userStatus = new UserStatus(user.getId());
        this.userStatusRepository.save(userStatus);

        return userRepository.save(user);
    }

    public void validateUniqueUsername(String username) {
         boolean isUniqueEmail = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
         if (!isUniqueEmail) {
             throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + username);
         }
    }

    public void validateUniqueEmail(String email) {
        boolean isUniqueEmail = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (!isUniqueEmail) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + email);
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.findUserStatus(user.getId());

        UserResponse response = this.toResponse(user);

        return response;
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(), user.getProfileId(),
                user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(),
                this.findUserStatus(user.getId()).isOnline()
        );
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request) {
        this.validateUniqueUsername(request.newUsername());
        this.validateUniqueEmail(request.newEmail());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = null;

        if (request.profile() != null) {
            var profile = request.profile();
            newProfileId = binaryContentRepository.save(
                    new BinaryContent(
                            profile.fileName(), profile.contentType(), profile.bytes()
                    )
            ).getId();
        }

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfileId);

        User saved = userRepository.save(user);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }
        return saved;
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User with id: " + userId + " not found."));

        UserStatus userStatus = this.findUserStatus(userId);
        UUID profileId = user.getProfileId();

        if (profileId != null) {
            this.binaryContentRepository.deleteById(profileId);
        }
        this.userStatusRepository.deleteById(userStatus.getId());
        userRepository.deleteById(userId);
    }

    public UserStatus findUserStatus(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found.");
        }

        List<UserStatus> userStatusList = this.userStatusRepository.findAll();
        UserStatus userStatus = userStatusList.stream()
                .filter(userStatus -> userStatus.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return userStatus;
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
