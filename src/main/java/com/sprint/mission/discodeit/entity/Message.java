package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

import java

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String content;
    private User authorId;
    private Channel channelId;
}
