package com.aliens.friendship.backend_chatting_server.websocket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkReadWebsocketResponse {
    private String requestId;
    private Long roomId;
    private String status;
}
