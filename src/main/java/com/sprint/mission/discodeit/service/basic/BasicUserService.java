package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

//    public BasicUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

//    @Override
//    public User create(String userName, String email, String password) {
//        User user = new User(userName, email, password);
//        return userRepository.save(user);
//    }

    @Override
    public UserResponse create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        User createdUser = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());
        User savedUser = userRepository.save(createdUser);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                null,
                false,
                savedUser.getCreatedAt()
        );
    }

//    @Override
//    public User find(UUID userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
//    }

    @Override
    public UserResponse find(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId)
                );

        boolean isOnline = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline) // 마지막 접속 5분 이내인지
                .orElse(false);

        UUID profileId = binaryContentRepository.findProfileByUserId(userId)
                .map(BinaryContent::getId)
                .orElse(null);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profileId,
                isOnline,
                user.getCreatedAt()
        );
    }

    @Override
    public List<User> findAll(UserResponse userResponse) {
        return List.of();
    }


    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> find(user.getId()))
                .toList();
    }


    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
