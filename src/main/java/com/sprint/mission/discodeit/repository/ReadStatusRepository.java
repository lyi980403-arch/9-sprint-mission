package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.status.ReadStatus;

import java.util.Optional;
import java.util.UUID;


public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
}
