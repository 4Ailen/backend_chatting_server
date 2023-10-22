package com.aliens.friendship.backend_chatting_server.websocket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkReadRequest {
    private String requestId;
    private Long partnerId;
    private Long roomId;
}