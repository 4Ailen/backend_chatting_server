package com.aliens.friendship.backend_chatting_server;

import com.aliens.friendship.backend_chatting_server.chatting.controller.ChatController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatResponseDto;
import com.aliens.friendship.backend_chatting_server.chatting.dto.NewChatWithCountOfUnreadChats;
import com.aliens.friendship.backend_chatting_server.jwt.JwtTokenProvider;
import com.aliens.friendship.backend_chatting_server.chatting.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    @DisplayName("성공_컨트롤러 새로운 채팅과 읽지 않은 채팅 개수 반환")
    public void testGetNewChatAndCountOfUnreadChats() throws Exception {
        String jwtToken = "dummyToken";
        Long currentMemberId = 123L;
        List<Long> roomIdsFromToken = Arrays.asList(1L, 2L, 3L);
        List<NewChatWithCountOfUnreadChats> expectedResponse = Arrays.asList(
                NewChatWithCountOfUnreadChats.builder()
                        .roomId(1L)
                        .countOfUnreadChats(1L)
                        .newChat("Hello")
                        .build(),
                NewChatWithCountOfUnreadChats.builder()
                        .roomId(2L)
                        .countOfUnreadChats(2L)
                        .newChat("Hi")
                        .build()
        );

        when(jwtTokenProvider.validateToken(jwtToken)).thenReturn(true);
        when(jwtTokenProvider.getRoomIdsFromToken(jwtToken)).thenReturn(roomIdsFromToken);
        when(jwtTokenProvider.getCurrentMemberIdFromToken
                (jwtToken)).thenReturn(currentMemberId);
        when(chatService.getNewChatAndNotReadCountOfChatInEachRoomsByRoomIds(currentMemberId, roomIdsFromToken)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/chat")
                        .header("ChattingToken", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].roomId", is(1)))
                .andExpect(jsonPath("$[0].countOfUnreadChats", is(1)))
                .andExpect(jsonPath("$[0].newChat", is("Hello")))
                .andExpect(jsonPath("$[1].roomId", is(2)))
                .andExpect(jsonPath("$[1].countOfUnreadChats", is(2)))
                .andExpect(jsonPath("$[1].newChat", is("Hi")));

        verify(jwtTokenProvider, times(1)).validateToken(jwtToken);
        verify(jwtTokenProvider, times(1)).getRoomIdsFromToken(jwtToken);
        verify(jwtTokenProvider, times(1)).getCurrentMemberIdFromToken(jwtToken);
        verify(chatService, times(1)).getNewChatAndNotReadCountOfChatInEachRoomsByRoomIds(currentMemberId, roomIdsFromToken);
    }

    @Test
    @DisplayName("성공_컨트롤러 채팅방 정보를 통한 신규채팅 100개 반환")
    public void testGetHundredChatsByRoomId() throws Exception {
        String jwtToken = "dummyToken";
        Long roomId = 1L;
        Long currentMemberId = 123L;
        List<Long> roomIdsFromToken = Arrays.asList(1L, 2L, 3L);
        List<ChatResponseDto> expectedResponse = Arrays.asList(
                ChatResponseDto.builder()
                        .chatId(1L)
                        .senderId(1L)
                        .receiverId(2L)
                        .message("Hello")
                        .build(),
                ChatResponseDto.builder()
                        .chatId(2L)
                        .senderId(2L)
                        .receiverId(1L)
                        .message("Hi")
                        .build()
        );

        when(jwtTokenProvider.validateToken(jwtToken)).thenReturn(true);
        when(jwtTokenProvider.getRoomIdsFromToken(jwtToken)).thenReturn(roomIdsFromToken);
        when(chatService.getHundredChatsByRoomId(roomId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/chat/{roomId}", roomId)
                        .header("ChattingToken", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].chatId", is(1)))
                .andExpect(jsonPath("$[0].senderId", is(1)))
                .andExpect(jsonPath("$[0].receiverId", is(2)))
                .andExpect(jsonPath("$[0].message", is("Hello")))
                .andExpect(jsonPath("$[1].chatId", is(2)))
                .andExpect(jsonPath("$[1].senderId", is(2)))
                .andExpect(jsonPath("$[1].receiverId", is(1)))
                .andExpect(jsonPath("$[1].message", is("Hi")));

        verify(jwtTokenProvider, times(1)).validateToken(jwtToken);
        verify(jwtTokenProvider, times(1)).getRoomIdsFromToken(jwtToken);
        verify(chatService, times(1)).getHundredChatsByRoomId(roomId);
    }

    // Helper method to convert objects to JSON string
    private String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

