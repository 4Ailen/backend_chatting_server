package com.aliens.friendship.backend_chatting_server.chatting.converter;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatType;
import com.aliens.friendship.backend_chatting_server.global.common.Converter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.ChatSendRequest;

@Converter
public class ChatConverter {

    public ChatEntity toChatEntityWithRequest(ChatSendRequest chatSendRequest) {
        return ChatEntity.builder()
                .roomId(chatSendRequest.getRoomId())
                .senderId(chatSendRequest.getSenderId())
                .senderName(chatSendRequest.getSenderName())
                .receiverId(chatSendRequest.getReceiverId())
                .chatType(ChatType.valueOf(chatSendRequest.getChatType()))
                .chatContent(chatSendRequest.getChatContent())
                .sendTime(chatSendRequest.getSendTime())
                .unreadCount(1)
                .build();
    }

    public ChatSendResponse toChatSendResponseWithEntity(ChatEntity chatEntity) {
        return ChatSendResponse.builder()
                .chatId(chatEntity.getChatId())
                .chatType(chatEntity.getChatType().getValue())
                .chatContent(chatEntity.getChatContent())
                .roomId(chatEntity.getRoomId())
                .senderId(chatEntity.getSenderId())
                .senderName(chatEntity.getSenderName())
                .receiverId(chatEntity.getReceiverId())
                .sendTime(chatEntity.getSendTime())
                .unreadCount(chatEntity.getUnreadCount())
                .build();
    }
}
