package com.aliens.friendship.backend_chatting_server.chatting.controller;

import com.aliens.friendship.backend_chatting_server.chatting.business.ChatBusiness;
import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSummariesResponse;
import com.aliens.friendship.backend_chatting_server.fcm.service.FcmService;
import com.aliens.friendship.backend_chatting_server.global.util.jwt.JwtTokenUtil;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final ChatBusiness chatBusiness;

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatSendResponse>> getUnreadChatsByRoomId(@RequestHeader("ChattingToken") String jwtToken, @PathVariable Long roomId) throws JsonProcessingException {
        return ResponseEntity.ok(chatBusiness.getUnreadChatsByRoomId(jwtToken, roomId));
    }

    @GetMapping("/chat/summary")
    public ResponseEntity<ChatSummariesResponse> getSummaryChatsByRoomId(@RequestHeader("ChattingToken") String jwtToken) {
        return ResponseEntity.ok(chatBusiness.getSummaryChats(jwtToken));
    }
}
