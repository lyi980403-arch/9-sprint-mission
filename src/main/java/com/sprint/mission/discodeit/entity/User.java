package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private String userName;
    private String email;
    private String password;

    private final long createdAt;
    private long updatedAt;

    public User(String userName, String email, String password) {
        this.userId = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = Instant.now().toEpochMilli();
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public deleteUser() {
        return null;
    }
}
