package com.aliens.friendship.backend_chatting_server.chatting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSummariesResponse {
    private List<ChatSummary> ChatSummaries = new ArrayList<>();
    public void addChatSummary(ChatSummary chatSummary) {
        this.ChatSummaries.add(chatSummary);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChatSummary {
        private Long roomId;
        private String lastChatContent;
        private String lastChatTime;
        private Long numberOfUnreadChat;
    }
}
