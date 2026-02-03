package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.status.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findById(UUID id);
    List<UserStatus> findAll();

    Optional<UserStatus> findByUserId(UserResponse userResponse);
}
