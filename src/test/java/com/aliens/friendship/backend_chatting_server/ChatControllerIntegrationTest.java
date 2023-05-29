package com.aliens.friendship.backend_chatting_server;

import com.aliens.friendship.backend_chatting_server.chatting.domain.ChatMessageCategory;
import com.aliens.friendship.backend_chatting_server.chatting.dto.ChatRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChatControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @DisplayName("성공_채팅 전송 및 저장")
    @Test
    public void testSendMessage() throws Exception {
        // 테스트에 필요한 데이터 설정
        String jwtToken = "dummyToken";
        ChatRequestDto chatRequestDto = ChatRequestDto.builder()
                .senderId(0L)
                .receiverId(0L)
                .messageCategory(ChatMessageCategory.NORMAL_MESSAGE)
                .roomId(1L)
                .message("안녕")
                .build();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("ChattingToken", jwtToken);

        // API 호출
        restTemplate.postForObject(
                "http://localhost:" + port + "/ws/api/chat/msg",
                new HttpEntity<>(chatRequestDto, headers),
                Void.class
        );

    }

    @DisplayName("성공_채팅 읽음 상태 변경")
    @Test
    public void testChangeReadStatus() throws Exception {
        // 테스트에 필요한 데이터 설정
        String jwtToken = "dummyToken";
        Long chatId = 123L;

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("ChattingToken", jwtToken);

        // API 호출
        restTemplate.postForObject(
                "http://localhost:" + port + "/ws/api/chat/msg/" + chatId,
                new HttpEntity<>(headers),
                Void.class
        );

    }
}