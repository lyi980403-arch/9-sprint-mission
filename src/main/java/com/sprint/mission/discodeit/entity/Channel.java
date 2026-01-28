package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String channelId;
    private ChannelType channelType;
    private String channelName;
    private String channelDescription;
}
