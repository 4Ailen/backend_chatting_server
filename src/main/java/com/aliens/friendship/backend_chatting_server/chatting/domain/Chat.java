package com.aliens.friendship.backend_chatting_server.chatting.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "chat")
public class Chat {
    @Transient
    public static final String SEQUENCE_NAME = "chat_sequence";

    @Id
    private Long chatId;

    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String message;
    private ChatMessageCategory messageCategory;
    private Instant createTime;
    private boolean read;

    public void setId(Long generateSequence) {
        this.chatId = generateSequence;
    }
    public boolean isRead(){
        return read;
    }

    public void changeReadState() {
        this.read = true;
    }

}
