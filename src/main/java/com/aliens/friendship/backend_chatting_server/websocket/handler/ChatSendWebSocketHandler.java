package com.aliens.friendship.backend_chatting_server.websocket.handler;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import com.aliens.friendship.backend_chatting_server.fcm.service.FcmService;
import com.aliens.friendship.backend_chatting_server.global.util.jwt.JwtTokenUtil;
import com.aliens.friendship.backend_chatting_server.websocket.converter.WebsocketConverter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.ChatSendRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.ChatSendWebsocketResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatSendWebSocketHandler extends TextWebSocketHandler {
    private List<Long> roomIds;
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;
    private final WebsocketConverter websocketConverter;

    private void sendChat(WebSocketSession session, TextMessage message, List<Long> roomIds) throws FirebaseMessagingException, IOException {
        // 요청 역직렬화
        ChatSendRequest chatSendRequest = objectMapper.readValue(message.getPayload(), ChatSendRequest.class);

        // roomId 검증
        chatService.validateRoomId(chatSendRequest.getRoomId(), roomIds);

        // 채팅 저장
        ChatSendResponse chatSendResponse = chatService.saveChat(chatSendRequest);

        // 상대방에게 새로운 채팅 FCM 전송
        fcmService.sendSingleChatToReceiverByToken(chatSendResponse);

        // 채팅 전송 요청에 대한 응답 생성
        ChatSendWebsocketResponse chatWebSocketResponse = websocketConverter.toChatSendWebsocketResponseWithRequest(chatSendRequest, chatSendResponse);

        // 응답 직렬화
        String response = objectMapper.writeValueAsString(chatWebSocketResponse);

        // response를 WebSocketSession에 전송
        session.sendMessage(new TextMessage(response));

        // log
        log.info("ChatSendWebSocketHandler.sendChat() response: {}", response);
    }

    private List<Long> afterWebsocketConnectionEstablished(WebSocketSession session) {
        // header JWT 토큰 가져오기
        String JWTToken = session.getHandshakeHeaders().get("Authorization").get(0);

        // 토큰 검증
        jwtTokenUtil.validateToken(JWTToken);

        // log
        log.info("ChatSendWebSocketHandler.afterWebsocketConnectionEstablished() JWTToken: {}", JWTToken);

        // header token에서 roomId 추출후 반환
        return jwtTokenUtil.getRoomIdsFromToken(JWTToken);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, FirebaseMessagingException {
        sendChat(session, message, roomIds);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        roomIds = afterWebsocketConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    }

}
