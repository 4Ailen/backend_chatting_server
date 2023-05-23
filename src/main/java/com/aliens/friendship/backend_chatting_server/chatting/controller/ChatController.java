package com.aliens.friendship.backend_chatting_server.chatting.controller;

import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.jwt.JwtTokenProvider;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations msgTemplate;
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/chat/msg")
    public void send(@RequestBody @Valid ChatRequestDto chatRequestDto) {
    }


    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatResponseDto>> getChatLogByRoomId(@RequestHeader("ChattingToken") String jwtToken,
                                                                    @PathVariable Long roomId) {
        List<Long> roomIds = jwtTokenProvider.getRoomIdsFromToken(jwtToken);

        List<ChatResponseDto> chatResponseDtos = roomIds.stream()
                .filter(jwtRoomId -> jwtRoomId.equals(roomId))
                .findFirst()
                .map(jwtRoomId -> chatService.getAllChatsByRoomId(roomId))
                .orElse(Collections.emptyList());

        return new ResponseEntity<>(chatResponseDtos, HttpStatus.OK);
    }


    @GetMapping("/chat")
    public ResponseEntity<List<List<ChatResponseDto>>> getChatRooms(@RequestHeader("ChattingToken") String jwtToken) {
        jwtTokenProvider.validateToken(jwtToken);
        List<Long> roomIdsFromToken = jwtTokenProvider.getRoomIdsFromToken(jwtToken);
        return new ResponseEntity<>(chatService.findAllChatsOfEachRoomsByRoomIds(roomIdsFromToken), HttpStatus.OK);
    }


    @GetMapping("/chat/read")
    public ResponseEntity<Integer> getChatLogNotRead(@RequestHeader("ChattingToken") String jwtToken, @RequestParam Long roomId) {
        jwtTokenProvider.validateToken(jwtToken);
        Long currentMemberIdFromToken = jwtTokenProvider.getCurrentMemberIdFromToken(jwtToken);
        List<Long> roomIds = jwtTokenProvider.getRoomIdsFromToken(jwtToken);

        Integer count = roomIds.stream()
                .filter(jwtRoomId -> jwtRoomId.equals(roomId))
                .findFirst()
                .map(jwtRoomId -> chatService.getCountOfNotReadChatByRoomIdAndMemberId(roomId, currentMemberIdFromToken))
                .orElse(0);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @GetMapping("/chat/read")
    public void getCountOfChatNotRead(@RequestParam Long chatId) {
        chatService.changeChatToReadByChatId(chatId);
    }


    @GetMapping("/chat/read")
    public void changeChatsNotReadByRoomId(@RequestHeader("ChattingToken") String jwtToken, @RequestParam Long roomId) {
        Long currentMemberIdFromToken = jwtTokenProvider.getCurrentMemberIdFromToken(jwtToken);
        List<Long> roomIds = jwtTokenProvider.getRoomIdsFromToken(jwtToken);

        roomIds.stream()
                .filter(jwtRoomId -> jwtRoomId.equals(roomId))
                .findFirst()
                .ifPresent(jwtRoomId -> chatService.changeAllChatOfRoomToReadByCurrentMemberIdAndRoomId(roomId, currentMemberIdFromToken));
    }


}
