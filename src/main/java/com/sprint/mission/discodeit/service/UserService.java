package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest);
    User find(UserResponse userResponse);
    List<User> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    void delete(UUID userId);
}
