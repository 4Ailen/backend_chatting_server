package com.aliens.friendship.backend_chatting_server.db.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "chats")
public class ChatEntity {
    @Transient
    public static final String SEQUENCE_NAME = "chat_sequence";

    @Id
    private Long chatId;
    private ChatType chatType;
    private String chatContent;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String sendTime;
    private int unreadCount;

    public void setId(Long generateSequence) {
        this.chatId = generateSequence;
    }
    public void changeReadState() {
        this.unreadCount = 0;
    }
}