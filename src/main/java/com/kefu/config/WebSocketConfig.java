package com.kefu.config;

import com.kefu.websocket.ChatWebSocketHandler;
import com.kefu.websocket.ChatWebSocketService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketService chatWebSocketService;

    public WebSocketConfig(ChatWebSocketService chatWebSocketService) {
        this.chatWebSocketService = chatWebSocketService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(chatWebSocketService), "/ws/chat")
                .setAllowedOriginPatterns("*");
    }
}
