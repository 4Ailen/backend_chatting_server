package com.aliens.friendship.backend_chatting_server.chatting.controller;

import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.NewChatWithCountOfUnreadChats;
import com.aliens.friendship.backend_chatting_server.jwt.JwtTokenProvider;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import com.google.api.Http;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations msgTemplate;
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    //채팅 전송 및 저장
    @MessageMapping("/chat/msg")
    public void sendMessage(@RequestHeader("ChattingToken") String jwtToken,@RequestBody @Valid ChatRequestDto chatRequestDto) {
        jwtTokenProvider.validateToken(jwtToken);
        chatService.addChat(chatRequestDto);
        // 비동기적으로 FCM 서비스 코드 추가 필요
    }

    @MessageMapping("/chat/msg/{chatId}")
    public void changeReadStatus(@RequestHeader("ChattingToken") String jwtToken,@PathVariable Long chatId) {
        jwtTokenProvider.validateToken(jwtToken);
        chatService.changeChatToReadByChatId(chatId);
    }

    // 현재 사용자의 jwt 기반으로 룸들의 새로운 채팅과 안읽은 채팅개수를 반환시켜준다 .
    @GetMapping("/chat")
    public ResponseEntity<List<NewChatWithCountOfUnreadChats>> getNewChatAndCountOfUnreadChats(@RequestHeader("ChattingToken") String jwtToken) {
        jwtTokenProvider.validateToken(jwtToken);
        List<Long> roomIdsFromToken = jwtTokenProvider.getRoomIdsFromToken(jwtToken);
        Long currentMemberId = jwtTokenProvider.getCurrentMemberIdFromToken(jwtToken);
        return new ResponseEntity<>(chatService.getNewChatAndNotReadCountOfChatInEachRoomsByRoomIds(currentMemberId,roomIdsFromToken), HttpStatus.OK);
    }

    //현재 사용자의 jwt 안에 RoomId와 현재 얻고자하는 roomId를 비교하여
    // 접근이 가능하다면 해당 채팅방의 100개 채팅내역을 가져온다.
    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatResponseDto>> getHundredChatsByRoomId(@RequestHeader("ChattingToken") String jwtToken, @PathVariable Long roomId) {
        jwtTokenProvider.validateToken(jwtToken);
        List<Long> roomIdsFromToken = jwtTokenProvider.getRoomIdsFromToken(jwtToken);

        boolean validRoomId = roomIdsFromToken.contains(roomId);

        return validRoomId
                ? ResponseEntity.ok(chatService.getHundredChatsByRoomId(roomId))
                : ResponseEntity.badRequest().build();
    }


}
