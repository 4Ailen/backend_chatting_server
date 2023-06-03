package com.aliens.friendship.backend_chatting_server.fcm.controller;

import com.aliens.friendship.backend_chatting_server.fcm.dto.MemberIdWithFCMTokenDto;
import com.aliens.friendship.backend_chatting_server.fcm.service.FCMService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FCMController.class)
class FCMControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FCMService FCMService;

    @Test
    void setFcmToken_Success() throws Exception {
        //given
        MemberIdWithFCMTokenDto memberIdWithFCMTokenDto = MemberIdWithFCMTokenDto.builder()
                .fcmToken("testFcmToken")
                .memberId(1L)
                .build();

        //when & then
        mockMvc.perform(post("/api/v1/fcm/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberIdWithFCMTokenDto)))
                .andExpect(status().isOk());
        verify(FCMService, times(1)).addFCMToken(memberIdWithFCMTokenDto);
    }

    @Test
    void pushMessageToAll_Success() throws Exception {
        //given
        Map<String, String> allNoticeMessage = new HashMap<>();
        allNoticeMessage.put("allNoticeMessage", "매칭이 완료되었습니다!");

        //when & then
        mockMvc.perform(post("/api/v1/fcm/all-notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(allNoticeMessage)))
                .andExpect(status().isOk());
        verify(FCMService, times(1)).sendNoticeToAll(allNoticeMessage.get("allNoticeMessage"));

    }
}