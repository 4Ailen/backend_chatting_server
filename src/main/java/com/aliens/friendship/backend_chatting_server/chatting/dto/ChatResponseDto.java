package com.aliens.friendship.backend_chatting_server.chatting.dto;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import com.aliens.friendship.backend_chatting_server.chatting.domain.ChatMessageCategory;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String message;
    private ChatMessageCategory messageCategory;

    private Long chatId;
    private String createTime;
    private boolean read;

    /* Entity -> Dto*/
    public ChatResponseDto(Chat chat) {
        this.roomId = chat.getRoomId();
        this.senderId = chat.getSenderId();
        this.receiverId = chat.getReceiverId();
        this.message = chat.getMessage();
        this.messageCategory = chat.getMessageCategory();
        this.chatId = chat.getChatId();
        this.createTime = chat.getCreateTime().toString();
        this.read = chat.isRead();
    }

    /* RequestDto -> ResponseDto*/
    public ChatResponseDto(ChatRequestDto requestDto) {
        this.roomId = requestDto.getRoomId();
        this.senderId = requestDto.getSenderId();
        this.receiverId = requestDto.getReceiverId();
        this.message = requestDto.getMessage();
        this.messageCategory = requestDto.getMessageCategory();
        this.createTime = LocalDateTime.now().toString();
        this.read = false;
    }
}