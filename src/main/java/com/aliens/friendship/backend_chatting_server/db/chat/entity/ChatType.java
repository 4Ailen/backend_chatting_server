package com.aliens.friendship.backend_chatting_server.db.chat.entity;

public enum ChatType {
    NORMAL_MESSAGE(0),
    VS_GAME_MESSAGE(1),
    ALL_NOTICE_MESSAGE(2);

    private int value;

    ChatType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChatType valueOf(int value) {
        for (ChatType chatType : ChatType.values()) {
            if (chatType.getValue() == value) {
                return chatType;
            }
        }
        throw new IllegalArgumentException("Invalid ChatType value: " + value);
    }
}