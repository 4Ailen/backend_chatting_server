package com.aliens.friendship.backend_chatting_server.chatting.service;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.NewChatWithCountOfUnreadChats;
import com.aliens.friendship.backend_chatting_server.chatting.repository.ChatRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public Chat addChat(ChatRequestDto chatRequestDto) {
        return chatRepository.save(chatRequestDto.toEntity());
    }


    // 단일 읽음처리
    public void changeChatToReadByChatId(Long chatId) {
        Optional<Chat> oneChat = chatRepository.findByChatId(chatId);
        oneChat.ifPresent(t->{
            t.changeReadState();
            this.chatRepository.save(t);
        });
    }


    //각 채팅방의 가장 최근 채팅과 안읽은 채팅개수
    public List<NewChatWithCountOfUnreadChats> getNewChatAndNotReadCountOfChatInEachRoomsByRoomIds(Long currentMemberId, List<Long> roomIds) {
        return roomIds.stream()
                .map(roomId -> {
                    NewChatWithCountOfUnreadChats dto = new NewChatWithCountOfUnreadChats();
                    Optional<Chat> oneChat = chatRepository.findNewOneChatByRoomId(roomId);
                    if (oneChat.isEmpty()) {
                        dto.setNewChat("채팅을 시작하세요!");
                    } else {
                        dto.setNewChat(oneChat.get().getMessage());
                    }
                    dto.setCountOfUnreadChats(chatRepository.countUnreadChatsByRoomIdAndReceiverId(roomId, currentMemberId));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // 채팅방 하나의 최근 채팅 100 개
    public List<ChatResponseDto> getHundredChatsByRoomId(Long roomId) {
        Optional<List<Chat>> chats = chatRepository.findHundredChatsByRoomId(roomId);
        ArrayList<ChatResponseDto> result = new ArrayList<>();
        if(chats.isPresent()){
            for(Chat chat : chats.get()){
                result.add(new ChatResponseDto(chat));
            }
            return result;
        }
        else{
            return null;
        }
    }
}