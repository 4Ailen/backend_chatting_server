package com.aliens.friendship.backend_chatting_server.fcm.converter;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import com.aliens.friendship.backend_chatting_server.global.common.Converter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.BulkReadRequest;
import com.google.firebase.messaging.MulticastMessage;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Converter
public class FcmMessageConverter {

    public MulticastMessage toChat(ChatSendResponse chatSendResponse, List<String> memberFcmTokens) {
        return MulticastMessage.builder()
                .putData("title", chatSendResponse.getSenderName())
                .putData("body", chatSendResponse.getChatContent())
                .putData("type", "chat")
                .putData("roomId", chatSendResponse.getRoomId().toString())
                .putData("senderId", chatSendResponse.getSenderId().toString())
                .putData("receiverId", chatSendResponse.getReceiverId().toString())
                .putData("chatContent", chatSendResponse.getChatContent())
                .putData("chatType", chatSendResponse.getChatType().toString())
                .putData("chatId", chatSendResponse.getChatId().toString())
                .putData("sendTime", chatSendResponse.getSendTime())
                .putData("unreadCount", chatSendResponse.getUnreadCount().toString())
                .addAllTokens(memberFcmTokens)
                .build();
    }

    public MulticastMessage toSingleRead(ChatEntity chatEntity, List<String> memberFcmTokens) {
        return MulticastMessage.builder()
                .addAllTokens(memberFcmTokens)
                .putData("type", "read")
                .putData("roomId", chatEntity.getRoomId().toString())
                .putData("chatId", chatEntity.getChatId().toString())
                .build();
    }

    public MulticastMessage toBulkRead(BulkReadRequest bulkReadRequest, List<String> memberFcmTokens) {
        return MulticastMessage.builder()
                .addAllTokens(memberFcmTokens)
                .putData("type", "bulkRead")
                .putData("roomId", bulkReadRequest.getRoomId().toString())
                .build();
    }

    public MulticastMessage toNotice(String title, String content, List<String> registeredTokens) {
        return MulticastMessage.builder()
                .putData("type", "notice")
                .putData("title", title)
                .putData("body", content)
                .addAllTokens(registeredTokens)
                .build();
    }
}