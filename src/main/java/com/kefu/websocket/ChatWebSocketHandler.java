package com.kefu.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatWebSocketService service;

    public ChatWebSocketHandler(ChatWebSocketService service) {
        this.service = service;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String key = extractKey(session.getUri());
        if (key == null) key = "global";
        session.getAttributes().put("ws_key", key);
        service.register(key, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Echo or broadcast incoming messages if they contain session info
        String payload = message.getPayload();
        String key = (String) session.getAttributes().get("ws_key");
        if (key != null) {
            // simple broadcast to same key
            service.broadcastToKey(key, Map.of("type", "message", "data", payload));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String key = (String) session.getAttributes().get("ws_key");
        if (key != null) service.unregister(key, session);
    }

    private String extractKey(URI uri) {
        if (uri == null) return null;
        String query = uri.getQuery();
        if (query == null) return null;
        Map<String, String> map = Arrays.stream(query.split("&"))
                .map(s -> s.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        if (map.containsKey("sessionId")) return map.get("sessionId");
        if (map.containsKey("agentAccount")) return "agent_" + map.get("agentAccount");
        return null;
    }
}
