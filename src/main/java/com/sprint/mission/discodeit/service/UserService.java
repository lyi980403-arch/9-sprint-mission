package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

//    private final UserRepository userRepository;
//    private final UserStatusRepository userStatusRepository;
//    private final BinaryContentRepository binaryContentRepository;

    UserResponse create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest);
    UserResponse find(UUID userId);
    List<User> findAll(UserResponse userResponse);
    User update(UUID userId, String newUserName, String newEmail, String newPassword);
    void delete(UUID userId);
}
