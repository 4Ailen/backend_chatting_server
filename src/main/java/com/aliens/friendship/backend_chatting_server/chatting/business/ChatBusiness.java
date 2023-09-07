package com.aliens.friendship.backend_chatting_server.chatting.business;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSummariesResponse;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import com.aliens.friendship.backend_chatting_server.fcm.service.FcmService;
import com.aliens.friendship.backend_chatting_server.global.common.Business;
import com.aliens.friendship.backend_chatting_server.global.util.jwt.JwtTokenUtil;
import com.aliens.friendship.backend_chatting_server.websocket.converter.WebsocketConverter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.BulkReadRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.ChatSendRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.SingleReadRequest;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.BulkReadWebsocketResponse;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.ChatSendWebsocketResponse;
import com.aliens.friendship.backend_chatting_server.websocket.dto.response.SingleReadWebsocketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Business
@RequiredArgsConstructor
public class ChatBusiness {
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;
    private final WebsocketConverter websocketConverter;

    public void sendChat(WebSocketSession session, TextMessage message, List<Long> roomIds) throws FirebaseMessagingException, IOException {
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
    }

    public void updateSingleRead(WebSocketSession session, TextMessage message, List<Long> roomIds) throws FirebaseMessagingException, IOException {
        // 요청 역직렬화
        SingleReadRequest singleReadRequest = objectMapper.readValue(message.getPayload(), SingleReadRequest.class);

        // roomId 검증
        chatService.validateRoomId(singleReadRequest.getRoomId(), roomIds);

        // chatId로 chatEntity 조회
        ChatEntity chatEntity = chatService.getChatEntity(singleReadRequest.getChatId());

        // 단일 메시지 읽음 상태 업데이트
        chatService.updateReadStateByChatId(chatEntity);

        // 상대방에게 읽음 처리 FCM 전송
        fcmService.sendSingleChatReadToSenderByToken(chatEntity);

        // 읽기 처리 요청에 대한 응답 생성
        SingleReadWebsocketResponse singleReadWebsocketResponse = websocketConverter.toSingleReadWebsocketResponseWithRequest(singleReadRequest);

        // 응답 직렬화
        String response = objectMapper.writeValueAsString(singleReadWebsocketResponse);

        // response를 WebSocketSession에 전송
        session.sendMessage(new TextMessage(response));
    }

    public void updateBulkRead(WebSocketSession session, TextMessage message, List<Long> roomIds) throws FirebaseMessagingException, IOException {
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
    }

    public List<Long> afterWebsocketConnectionEstablished(WebSocketSession session) {
        // header JWT 토큰 가져오기
        String JWTToken = session.getHandshakeHeaders().get("Authorization").get(0);

        // 토큰 검증
        jwtTokenUtil.validateToken(JWTToken);

        // header token에서 roomId 추출후 반환
        return jwtTokenUtil.getRoomIdsFromToken(JWTToken);
    }

    public List<ChatSendResponse> getUnreadChatsByRoomId(String jwtToken, Long roomId) throws JsonProcessingException {
        // 유효 roomId 추출
        List<Long> roomIdsFromJwt = jwtTokenUtil.getRoomIdsFromToken(jwtToken);

        // roomId 검증
        chatService.validateRoomId(roomId, roomIdsFromJwt);

        // 읽지 않은 채팅 목록 반환
        return chatService.getUnreadChatsByRoomId(roomId);
    }

    public ChatSummariesResponse getSummaryChats(String jwtToken) {
        // 유효 roomId 추출
        List<Long> roomIdsFromJwt = jwtTokenUtil.getRoomIdsFromToken(jwtToken);

        // 요청한 멤버의 memberId 추출
        Long memberId = jwtTokenUtil.getCurrentMemberIdFromToken(jwtToken);

        // 채팅 요약 정보 반환
        return chatService.getChatSummaries(roomIdsFromJwt, memberId);
    }
}
