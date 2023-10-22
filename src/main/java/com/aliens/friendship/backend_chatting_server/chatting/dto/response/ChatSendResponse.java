package com.aliens.friendship.backend_chatting_server.chatting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSendResponse {
    private Long chatId;
    private Integer chatType;
    private String chatContent;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String sendTime;
    private Integer unreadCount;
}