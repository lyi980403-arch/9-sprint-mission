package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class RPConfig {
    // Repository Beans
    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository();
    }

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {return new UserStatusRepository() {
        @Override
        public UserStatus save(UserStatus userStatus) {
            return null;
        }

        @Override
        public Optional<UserStatus> findById(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<UserStatus> findAll() {
            return List.of();
        }

        @Override
        public Optional<UserStatus> findByUserId(UserResponse userResponse) {
            return Optional.empty();
        }
    };

    }

    @Bean
    public ReadStatusRepository readStatusRepository()

}
