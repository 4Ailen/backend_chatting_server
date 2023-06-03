package com.aliens.friendship.backend_chatting_server.chatting.service;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import com.aliens.friendship.backend_chatting_server.chatting.domain.ChatMessageCategory;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.NewChatWithCountOfUnreadChats;
import com.aliens.friendship.backend_chatting_server.chatting.repository.ChatRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    //채팅 저장
    public ChatResponseDto addChat(ChatRequestDto chatRequestDto) {
        Chat singleChat = chatRepository.save(chatRequestDto.toEntity());
        return new ChatResponseDto(singleChat);
    }


    // 단일 읽음처리
    public ChatResponseDto changeChatToReadByChatId(Long chatId) {
        Optional<Chat> singleChat = chatRepository.findByChatId(chatId);
        singleChat.ifPresent(t -> {
            t.changeReadState();
            this.chatRepository.save(t);
        });
        return new ChatResponseDto(chatRepository.findByChatId(chatId).get());
    }


    //각 채팅방의 가장 최근 채팅과 안읽은 채팅개수 반환
    public List<NewChatWithCountOfUnreadChats> getNewChatAndNotReadCountOfChatInEachRoomsByRoomIds(Long currentMemberId, List<Long> roomIds) {
        return roomIds.stream()
                .map(roomId -> {
                    NewChatWithCountOfUnreadChats dto = new NewChatWithCountOfUnreadChats();
                    Optional<Chat> oneChat = chatRepository.findNewOneChatByRoomId(roomId);
                    dto.setRoomId(roomId);
                    dto.setNewChat(oneChat.map(Chat::getMessage).orElse("채팅을 시작하세요!"));
                    dto.setCountOfUnreadChats(chatRepository.countUnreadChatsByRoomIdAndReceiverId(roomId, currentMemberId));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // 채팅방 하나의 최근 채팅 최대 100개 반환
    public List<ChatResponseDto> getHundredChatsByRoomId(Long roomId) {
        Optional<List<Chat>> chats = chatRepository.findHundredChatsByRoomId(roomId);
        List<ChatResponseDto> result = new ArrayList<>();

        chats.ifPresentOrElse(
                c -> c.stream()
                        .map(ChatResponseDto::new)
                        .forEach(result::add),
                () -> {
                    Chat firstChat = new Chat(0L, 0L, 0L, 0L, "채팅을 시작하세요!", ChatMessageCategory.ALL_NOTICE_MESSAGE, Instant.now(), true);
                    result.add(new ChatResponseDto(firstChat));
                }
        );

        return result;
    }

}