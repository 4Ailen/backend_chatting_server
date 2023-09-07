package com.aliens.friendship.backend_chatting_server.websocket.converter;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.global.common.Converter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.BulkReadRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.ChatSendRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.SingleReadRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.BulkReadWebsocketResponse;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.ChatSendWebsocketResponse;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.SingleReadWebsocketResponse;

@Converter
public class WebsocketConverter {
    public ChatSendWebsocketResponse toChatSendWebsocketResponseWithRequest(ChatSendRequest chatSendRequest, ChatSendResponse chatSendResponse) {
        return ChatSendWebsocketResponse.builder()
                .requestId(chatSendRequest.getRequestId())
                .chatId(chatSendResponse.getChatId())
                .status("success")
                .build();
    }

    public ChatSendWebsocketResponse toFailChatSendWebsocketResponseWithRequest(ChatSendRequest chatSendRequest, String status) {
        return ChatSendWebsocketResponse.builder()
                .requestId(chatSendRequest.getRequestId())
                .chatId(-1L)
                .status(status)
                .build();
    }

    public SingleReadWebsocketResponse toSingleReadWebsocketResponseWithRequest(SingleReadRequest singleReadRequest) {
        return SingleReadWebsocketResponse.builder()
                .requestId(singleReadRequest.getRequestId())
                .chatId(singleReadRequest.getChatId())
                .status("success")
                .build();
    }

    public SingleReadWebsocketResponse toFailSingleReadWebsocketResponseWithRequest(SingleReadRequest singleReadRequest, String status) {
        return SingleReadWebsocketResponse.builder()
                .requestId(singleReadRequest.getRequestId())
                .chatId(-1L)
                .status(status)
                .build();
    }

    public BulkReadWebsocketResponse toBulkReadWebsocketResponseWithRequest(BulkReadRequest bulkReadRequest) {
        return BulkReadWebsocketResponse.builder()
                .requestId(bulkReadRequest.getRequestId())
                .roomId(bulkReadRequest.getRoomId())
                .status("success")
                .build();
    }

    public BulkReadWebsocketResponse toFailBulkReadWebsocketResponseWithRequest(BulkReadRequest bulkReadRequest, String status) {
        return BulkReadWebsocketResponse.builder()
                .requestId(bulkReadRequest.getRequestId())
                .roomId(-1L)
                .status(status)
                .build();
    }
}
