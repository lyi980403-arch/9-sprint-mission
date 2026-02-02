package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;

    private final List<UUID> attachmentIds;

    public Message(String content, UUID channelId,
                   UUID authorId, List<UUID> attachmentIds, Instant createdAt)
    {
        this.id = UUID.randomUUID();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;

        this.attachmentIds = attachmentIds;
    }

    public void updateContent(String newContent){
        this.content = newContent;
        this.updatedAt = Instant.now();
    }

//    public UUID getId() {
//        return id;
//    }
//
//    public Long getCreatedAt() {
//        return createdAt;
//    }
//
//    public Long getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public UUID getChannelId() {
//        return channelId;
//    }
//
//    public UUID getAuthorId() {
//        return authorId;
//    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
