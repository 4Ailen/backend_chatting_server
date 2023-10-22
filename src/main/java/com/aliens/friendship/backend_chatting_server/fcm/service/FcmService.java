package com.aliens.friendship.backend_chatting_server.fcm.service;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import com.aliens.friendship.backend_chatting_server.db.fcm.entity.FcmTokenEntity;
import com.aliens.friendship.backend_chatting_server.fcm.config.FirebaseMessagingWrapper;
import com.aliens.friendship.backend_chatting_server.db.fcm.repository.FcmTokenRepository;
import com.aliens.friendship.backend_chatting_server.fcm.converter.FcmMessageConverter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.BulkReadRequest;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessagingWrapper firebaseMessagingWrapper;
    private final FcmMessageConverter fcmMessageConverter;

    public void sendSingleChatToReceiverByToken(ChatSendResponse chatSendResponse) throws FirebaseMessagingException {
        List<String> fcmTokens = getFcmTokens(chatSendResponse.getReceiverId());
        MulticastMessage message = fcmMessageConverter.toChat(chatSendResponse,fcmTokens);
        firebaseMessagingWrapper.sendMulticast(message);
    }

    public void sendSingleChatReadToSenderByToken(ChatEntity chatEntity) throws FirebaseMessagingException {
        List<String> fcmTokens = getFcmTokens(chatEntity.getSenderId());
        MulticastMessage message = fcmMessageConverter.toSingleRead(chatEntity, fcmTokens);
        firebaseMessagingWrapper.sendMulticast(message);
    }

    public void sendBulkChatReadToSenderByToken(BulkReadRequest bulkReadRequest) throws FirebaseMessagingException {
        List<String> fcmTokens = getFcmTokens(bulkReadRequest.getPartnerId());
        MulticastMessage message = fcmMessageConverter.toBulkRead(bulkReadRequest, fcmTokens);
        firebaseMessagingWrapper.sendMulticast(message);
    }

    public void sendNoticeToAll(String title, String content) throws FirebaseMessagingException {
        List<String> registeredTokens = getAllTokens();
        MulticastMessage message = fcmMessageConverter.toNotice(title, content, registeredTokens);
        firebaseMessagingWrapper.sendMulticast(message);
    }

    public List<String> getFcmTokens(Long memberId) {
        return fcmTokenRepository.findAllByMemberId(memberId)
                .stream()
                .map(FcmTokenEntity::getValue)
                .collect(Collectors.toList());
    }

    public List<String> getAllTokens() {
        return fcmTokenRepository.findAll()
                .stream()
                .map(FcmTokenEntity::getValue)
                .collect(Collectors.toList());
    }
}