package com.aliens.friendship.backend_chatting_server;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import com.aliens.friendship.backend_chatting_server.chatting.domain.ChatMessageCategory;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.NewChatWithCountOfUnreadChats;
import com.aliens.friendship.backend_chatting_server.chatting.repository.ChatRepository;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    @DisplayName("성공_채팅 저장")
    @Test
    public void testAddChat() {
        ChatRequestDto chatRequestDto = ChatRequestDto.builder()
                .roomId(125L)
                .message("안녕")
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .receiverId(0L)
                .senderId(1L)
                .build();
        Chat savedChat = chatRequestDto.toEntity();

        when(chatRepository.save(any(Chat.class))).thenReturn(savedChat);

        Chat result = chatService.addChat(chatRequestDto);

        assertNotNull(result);
    }

    @DisplayName("성공_ChatId를 통한 읽음처리")
    @Test
    public void testChangeChatToReadByChatId() {
        // 테스트에 필요한 데이터 설정
        Long chatId = 123L;
        Chat chat = Chat.builder()
                .read(false)
                .chatId(123L)
                .createTime(Instant.now())
                .receiverId(1L)
                .senderId(0L)
                .message("읽었니?")
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .roomId(3L)
                .build();
        when(chatRepository.findByChatId(chatId)).thenReturn(Optional.of(chat));

        chatService.changeChatToReadByChatId(chatId);

        verify(chatRepository, times(1)).save(chat);
    }

    @DisplayName("성공_최신채팅과 읽지않은 개수 반환")
    @Test
    public void testGetNewChatAndNotReadCountOfChatInEachRoomsByRoomIds() {
        Long currentMemberId = 123L;
        List<Long> roomIds = Arrays.asList(1L, 2L, 3L);

        when(chatRepository.findNewOneChatByRoomId(anyLong())).thenReturn(Optional.empty());
        when(chatRepository.countUnreadChatsByRoomIdAndReceiverId(anyLong(), anyLong())).thenReturn(0L);

        List<NewChatWithCountOfUnreadChats> result = chatService.getNewChatAndNotReadCountOfChatInEachRoomsByRoomIds(currentMemberId, roomIds);

        assertNotNull(result);
    }

    @DisplayName("성공_RoomId를 통한100개의 신규채팅 반환")
    @Test
    public void testGetHundredChatsByRoomId() {
        Long roomId = 123L;
        Chat chat1 = Chat.builder()
                .roomId(1L)
                .chatId(1L)
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .senderId(1L)
                .receiverId(1L)
                .createTime(Instant.now())
                .read(false)
                .message("안녕")
                .build();
        Chat chat2 = Chat.builder()
                .roomId(2L)
                .chatId(2L)
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .senderId(1L)
                .receiverId(1L)
                .createTime(Instant.now())
                .read(false)
                .message("안녕")
                .build();
        Chat chat3 = Chat.builder()
                .roomId(3L)
                .chatId(3L)
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .senderId(1L)
                .receiverId(1L)
                .createTime(Instant.now())
                .read(false)
                .message("안녕")
                .build();
        List<Chat> chats = Arrays.asList(chat1,chat2,chat3);
        when(chatRepository.findHundredChatsByRoomId(roomId)).thenReturn(Optional.of(chats));

        List<ChatResponseDto> result = chatService.getHundredChatsByRoomId(roomId);

        assertNotNull(result);
    }
}