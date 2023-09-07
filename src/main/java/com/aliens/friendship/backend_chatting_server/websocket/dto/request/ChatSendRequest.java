package com.aliens.friendship.backend_chatting_server.websocket.dto.request;

import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSendRequest {
    private String requestId;
    private Integer chatType;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String chatContent;
    private String sendTime;
}