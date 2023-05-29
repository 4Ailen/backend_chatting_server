package com.aliens.friendship.backend_chatting_server.chatting.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewChatWithCountOfUnreadChats {
    private Long roomId;
    private String newChat;
    private Long countOfUnreadChats;
}
