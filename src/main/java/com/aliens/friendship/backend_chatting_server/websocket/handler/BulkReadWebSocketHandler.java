package com.aliens.friendship.backend_chatting_server.websocket.handler;

import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import com.aliens.friendship.backend_chatting_server.fcm.service.FcmService;
import com.aliens.friendship.backend_chatting_server.global.util.jwt.JwtTokenUtil;
import com.aliens.friendship.backend_chatting_server.websocket.converter.WebsocketConverter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.BulkReadRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.BulkReadWebsocketResponse;
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
public class BulkReadWebSocketHandler extends TextWebSocketHandler {
    private List<Long> roomIds;
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;
    private final WebsocketConverter websocketConverter;

    private void updateBulkRead(WebSocketSession session, TextMessage message, List<Long> roomIds) throws FirebaseMessagingException, IOException {
        // 요청 역직렬화
        BulkReadRequest bulkReadRequest = objectMapper.readValue(message.getPayload(), BulkReadRequest.class);

        // roomId 검증
        chatService.validateRoomId(bulkReadRequest.getRoomId(), roomIds);

        // 채팅방기준 일괄 읽음 상태 업데이트
        Long chatId = chatService.updateReadStateByRoomId(bulkReadRequest.getRoomId(), bulkReadRequest.getPartnerId());

        // 상대방에게 채팅방 기준 일괄 읽음 처리 FCM 전송
        fcmService.sendBulkChatReadToSenderByToken(bulkReadRequest);

        // 일괄 처리 요청에 대한 웹소켓 응답 생성
        BulkReadWebsocketResponse readByRoomWebsocketResponse = websocketConverter.toBulkReadWebsocketResponseWithRequest(bulkReadRequest);

        // 응답 직렬화
        String response = objectMapper.writeValueAsString(readByRoomWebsocketResponse);

        // response를 WebSocketSession에 전송
        session.sendMessage(new TextMessage(response));

        // log
        log.info("BulkReadWebSocketHandler.updateBulkRead(): " + response);
    }

    private List<Long> afterWebsocketConnectionEstablished(WebSocketSession session) {
        // header JWT 토큰 가져오기
        String JWTToken = session.getHandshakeHeaders().get("Authorization").get(0);

        // 토큰 검증
        jwtTokenUtil.validateToken(JWTToken);

        // log
        log.info("BulkReadWebSocketHandler.afterWebsocketConnectionEstablished(): " + JWTToken);

        // header token에서 roomId 추출후 반환
        return jwtTokenUtil.getRoomIdsFromToken(JWTToken);
    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, FirebaseMessagingException {
        updateBulkRead(session, message, roomIds);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        roomIds = afterWebsocketConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    }

}
