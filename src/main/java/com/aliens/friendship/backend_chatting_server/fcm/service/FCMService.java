package com.aliens.friendship.backend_chatting_server.fcm.service;

import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.fcm.config.FirebaseMessagingWrapper;
import com.aliens.friendship.backend_chatting_server.fcm.domain.FcmToken;
import com.aliens.friendship.backend_chatting_server.fcm.dto.MemberIdWithFCMTokenDto;
import com.aliens.friendship.backend_chatting_server.fcm.repository.FcmRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final FcmRepository fcmRepository;
    private final FirebaseMessagingWrapper firebaseMessagingWrapper;

    public String getFcmToken(Long receiverId) {
        Optional<FcmToken> fcmToken = fcmRepository.findById(receiverId);
        return fcmToken.get().getToken();
    }

    public void addFCMToken(MemberIdWithFCMTokenDto memberWithFCMTokenDto) {
        fcmRepository.save(new FcmToken(memberWithFCMTokenDto.getMemberId(), memberWithFCMTokenDto.getFcmToken()));
    }

    // 수신자에게 푸시 알림
    public void sendSingleChatToReceiverByToken(ChatResponseDto chatResponseDto) {
        String fcmToken = getFcmToken(chatResponseDto.getReceiverId());
        if (fcmToken != null) {
            Notification notification = Notification.builder()
                    .setTitle("발신자 이름")
                    .setBody(chatResponseDto.getMessage())
                    .build();

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putData("roomId", chatResponseDto.getRoomId().toString())
                    .putData("senderId", chatResponseDto.getSenderId().toString())
                    .putData("receiverId", chatResponseDto.getReceiverId().toString())
                    .putData("message", chatResponseDto.getMessage())
                    .putData("chatMessageCategory", chatResponseDto.getMessageCategory().toString())
                    .putData("chatId", chatResponseDto.getChatId().toString())
                    .putData("createTime", chatResponseDto.getCreateTime())
                    .putData("read", chatResponseDto.getRead())
                    .build();

            firebaseMessagingWrapper.sendAsync(message);
        }
    }

    // 송신자에게 푸시 알림으로 수신자가 채팅을 읽었음을 알림.
    public void sendReadChatToSenderByToken(ChatResponseDto chatResponseDto) {
        String fcmToken = getFcmToken(chatResponseDto.getSenderId());
        if (fcmToken != null) {
            Notification notification = Notification.builder()
                    .setTitle("읽음 처리")
                    .setBody(chatResponseDto.getMessage())
                    .build();

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putData("roomId", chatResponseDto.getRoomId().toString())
                    .putData("senderId", chatResponseDto.getSenderId().toString())
                    .putData("receiverId", chatResponseDto.getReceiverId().toString())
                    .putData("message", chatResponseDto.getMessage())
                    .putData("chatMessageCategory", chatResponseDto.getMessageCategory().toString())
                    .putData("chatId", chatResponseDto.getChatId().toString())
                    .putData("createTime", chatResponseDto.getCreateTime())
                    .putData("read", chatResponseDto.getRead())
                    .build();

            firebaseMessagingWrapper.sendAsync(message);
        }
    }

    // 전체 공지 푸시 알림
    public void sendNoticeToAll(String allNoticeMessage) throws FirebaseMessagingException {
        List<String> registeredTokens = getAllTokens();

        Notification notification = Notification.builder()
                .setTitle("[FriendShip] 전체 공지")
                .setBody(allNoticeMessage)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(registeredTokens)
                .build();

        firebaseMessagingWrapper.sendMulticast(message);
    }

    public List<String> getAllTokens() {
        List<FcmToken> fcmTokens = fcmRepository.findAll();
        List<String> tokens = new ArrayList<>();
        for (FcmToken fcmToken : fcmTokens) {
            tokens.add(fcmToken.getToken());
        }
        return tokens;
    }

}