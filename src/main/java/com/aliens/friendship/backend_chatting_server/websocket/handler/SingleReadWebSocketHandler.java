package com.aliens.friendship.backend_chatting_server.websocket.handler;

import com.aliens.friendship.backend_chatting_server.chatting.business.ChatBusiness;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SingleReadWebSocketHandler extends TextWebSocketHandler {
    private List<Long> roomIds;
    private ChatBusiness chatBusiness;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, FirebaseMessagingException {
        chatBusiness.updateSingleRead(session, message, roomIds);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        roomIds = chatBusiness.afterWebsocketConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    }
}


