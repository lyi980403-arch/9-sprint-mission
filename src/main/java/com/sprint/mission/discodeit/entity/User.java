package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private UUID profileId;
    //
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        //
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }


    public void update(String newUsername, String newEmail, String newPassword) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
