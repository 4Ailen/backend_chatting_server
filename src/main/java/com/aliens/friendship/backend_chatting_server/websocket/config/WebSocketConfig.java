package com.aliens.friendship.backend_chatting_server.websocket.config;


import com.aliens.friendship.backend_chatting_server.websocket.handler.BulkReadWebSocketHandler;
import com.aliens.friendship.backend_chatting_server.websocket.handler.ChatSendWebSocketHandler;
import com.aliens.friendship.backend_chatting_server.websocket.handler.SingleReadWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    final private ChatSendWebSocketHandler chatSendWebSocketHandler;
    final private BulkReadWebSocketHandler bulkReadWebSocketHandler;
    final private SingleReadWebSocketHandler singleReadWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatSendWebSocketHandler, "/ws/chat/message/send").setAllowedOrigins("*");
        registry.addHandler(singleReadWebSocketHandler, "/ws/chat/message/read").setAllowedOrigins("*");
        registry.addHandler(bulkReadWebSocketHandler, "/ws/chat/room/read").setAllowedOrigins("*");
    }
}
