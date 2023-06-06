package com.aliens.friendship.backend_chatting_server.fcm.service;

import com.aliens.friendship.backend_chatting_server.chatting.domain.ChatMessageCategory;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.fcm.config.FirebaseMessagingWrapper;
import com.aliens.friendship.backend_chatting_server.fcm.domain.FcmToken;
import com.aliens.friendship.backend_chatting_server.fcm.dto.MemberIdWithFCMTokenDto;
import com.aliens.friendship.backend_chatting_server.fcm.repository.FcmRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class FCMServiceTest {

    @InjectMocks
    FCMService FCMService;

    @Mock
    FirebaseMessagingWrapper firebaseMessagingWrapper;

    @Mock
    FcmRepository fcmRepository;

    @Test
    @DisplayName("fcm 토큰 등록 성공")
    void AddFCMToken_Success_When_GivenValidMemberIdAndFCMToken() {
        //given
        MemberIdWithFCMTokenDto memberIdWithFCMTokenDto = MemberIdWithFCMTokenDto.builder()
                .fcmToken("testFcmToken")
                .memberId(1L)
                .build();

        //when
        FCMService.addFCMToken(memberIdWithFCMTokenDto);

        //then
        verify(fcmRepository, times(1)).save(any(FcmToken.class));
    }

    @Test
    @DisplayName("수신자에게 하나의 채팅 푸시 알림 성공")
    void sendSingleChatToReceiverByToken_Success_When_GivenValidChatResponseDto() {
        //given
        FcmToken receiverFcmToken = FcmToken.builder()
                .token("receiverFcmToken")
                .memberId(1L)
                .build();
        ChatResponseDto chatResponseDto = ChatResponseDto.builder()
                .roomId(1L)
                .senderId(2L)
                .receiverId(1L)
                .message("testMessage")
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .chatId(1L)
                .createTime(Instant.now().toString())
                .read("false")
                .build();
        when(fcmRepository.findById(chatResponseDto.getReceiverId())).thenReturn(Optional.ofNullable(receiverFcmToken));
        doNothing().when(firebaseMessagingWrapper).sendAsync(any(Message.class));

        //when
        FCMService.sendSingleChatToReceiverByToken(chatResponseDto);

        //then
        verify(firebaseMessagingWrapper, times(1)).sendAsync(any(Message.class));
    }

    @Test
    @DisplayName("수신자가 읽은 하나의 채팅을 송신자에게 푸시 알림 성공")
    void sendReadChatToSenderByToken_Success_When_GivenValidChatResponseDto() {
        //given
        FcmToken senderFcmToken = FcmToken.builder()
                .token("senderFcmToken")
                .memberId(1L)
                .build();
        ChatResponseDto chatResponseDto = ChatResponseDto.builder()
                .roomId(1L)
                .senderId(1L)
                .receiverId(2L)
                .message("testMessage")
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .chatId(1L)
                .createTime(Instant.now().toString())
                .read("true")
                .build();
        when(fcmRepository.findById(chatResponseDto.getSenderId())).thenReturn(Optional.ofNullable(senderFcmToken));
        doNothing().when(firebaseMessagingWrapper).sendAsync(any(Message.class));

        //when
        FCMService.sendReadChatToSenderByToken(chatResponseDto);

        //then
        verify(firebaseMessagingWrapper, times(1)).sendAsync(any(Message.class));
    }

    @Test
    @DisplayName("전체 공지 메시지 푸시 알림 성공")
    void sendNoticeToAll_Success_When_GivenNoticeAllMessage() throws FirebaseMessagingException {
        //given
        List<FcmToken> fcmTokens = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            fcmTokens.add(FcmToken.builder()
                    .token("senderFcmToken")
                    .memberId((long) i)
                    .build());
        }
        String noticeAllMessage = "매칭이 완료되었습니다!";
        when(fcmRepository.findAll()).thenReturn(fcmTokens);

        //when
        FCMService.sendNoticeToAll(noticeAllMessage);

        //then
        verify(firebaseMessagingWrapper, times(1)).sendMulticast(any(MulticastMessage.class));
    }
}