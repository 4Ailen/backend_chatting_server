package com.aliens.friendship.backend_chatting_server.fcm.controller;

import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.fcm.dto.MemberIdWithFCMTokenDto;
import com.aliens.friendship.backend_chatting_server.fcm.service.FCMService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FCMController {

    private final FCMService FCMService;

    @PostMapping("/token")
    public void setFcmToken(@RequestBody MemberIdWithFCMTokenDto memberWithFCMTokenDto) throws FirebaseMessagingException {
        FCMService.addFCMToken(memberWithFCMTokenDto);
    }

    @PostMapping("/all-notice")
    public void pushMessageToAll(@RequestBody Map<String, String> allNoticeMessage) throws FirebaseMessagingException {
        FCMService.sendNoticeToAll(allNoticeMessage.get("allNoticeMessage"));
    }

}