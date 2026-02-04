package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.Optional;
import java.util.UUID;

public class JCFUserStatusRepository implements UserStatusRepository {

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        return userStatus;
    }

    @Override
    public void deleteByUserId(UUID userId) {
    }
}
