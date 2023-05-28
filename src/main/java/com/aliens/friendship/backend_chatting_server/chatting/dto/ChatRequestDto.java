package com.aliens.friendship.backend_chatting_server.chatting.dto;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import com.aliens.friendship.backend_chatting_server.chatting.domain.ChatMessageCategory;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequestDto {

    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String message;
    private ChatMessageCategory messageCategory;

    /* Dto -> Entity */
    public Chat toEntity() {
        return Chat.builder()
                .roomId(this.getRoomId())
                .senderId(this.getSenderId())
                .receiverId(this.getReceiverId())
                .message(this.getMessage())
                .messageCategory(this.getMessageCategory())
                .createTime(Instant.now())
                .read(false)
                .build();
    }
}