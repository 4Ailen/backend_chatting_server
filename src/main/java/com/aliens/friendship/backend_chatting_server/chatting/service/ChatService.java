package com.aliens.friendship.backend_chatting_server.chatting.service;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.chatting.repository.ChatRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<ChatResponseDto> getAllChatsByRoomId(Long roomId) {
        return chatRepository.findAllByRoomId(roomId).stream()
                .sorted(Comparator.comparing(Chat::getChatId))
                .map(ChatResponseDto::new)
                .collect(Collectors.toList());
    }

    public int getCountOfNotReadChatByRoomIdAndMemberId(Long roomId, Long currentMemberId) {
        return chatRepository.findAllByRoomIdAndReceiverIdAndReadFalse(roomId, currentMemberId).size();
    }

    //fcm을 통해 메시지를 축적하게된다면.. 녀석이 필요 없지 않을까..라는 생각이 듭니다.
    public ChatResponseDto getLastNotReadChatByRoomId(Long roomId) {
        List<ChatResponseDto> chats = getAllChatsByRoomId(roomId);
        return chats.get(chats.size()-1);
    }

    public void changeChatsAllRead(Long roomId, Long currentMemberId ) {
        List<Chat> chats = chatRepository.findAllByRoomIdAndReceiverIdAndReadFalse(roomId, currentMemberId);
        chats.forEach(Chat::changeReadState);
        chatRepository.saveAll(chats);
    }

    public void changeChatToRead(Long chatId) {
        Chat oneChat = chatRepository.findByChatId(chatId);
        oneChat.changeReadState();
        chatRepository.save(oneChat);
    }

    public List<List<ChatResponseDto>> findAllChatsOfEachRoomsByRoomIds(List<Long> roomIds) {
        return roomIds.stream()
                .map(this::getAllChatsByRoomId)
                .collect(Collectors.toList());
    }

    public void addChatLog(ChatRequestDto chatRequestDto) {
        chatRepository.save(chatRequestDto.toEntity());
    }

    // 내부 조건에 따른 필터링과 DB쿼리문의 성능차이 실험을 위한 코드
    public int experimentGetCountOfNotReadChatByRoomIdAndMemberId(Long roomId, Long currentMemberId) {
        List<ChatResponseDto> chats = getAllChatsByRoomId(roomId);
        return (int) chats.stream()
                .filter(chat -> !chat.isRead() &&(chat.getReceiverId().equals(currentMemberId)) )
                .count();
    }
}