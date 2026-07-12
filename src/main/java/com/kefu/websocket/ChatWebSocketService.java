package com.kefu.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatWebSocketService {

    private final ConcurrentHashMap<String, ConcurrentHashMap.KeySetView<WebSocketSession, Boolean>> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public void register(String key, WebSocketSession session) {
        sessions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unregister(String key, WebSocketSession session) {
        Set<WebSocketSession> set = sessions.get(key);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) sessions.remove(key);
        }
    }

    public void broadcastToKey(String key, Map<String, Object> payload) {
        try {
            String text = mapper.writeValueAsString(payload);
            Set<WebSocketSession> set = sessions.get(key);
            if (set != null) {
                for (WebSocketSession s : set) {
                    if (s.isOpen()) {
                        try { s.sendMessage(new TextMessage(text)); } catch (IOException ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public void broadcastToAll(Map<String, Object> payload) {
        try {
            String text = mapper.writeValueAsString(payload);
            for (Set<WebSocketSession> set : sessions.values()) {
                for (WebSocketSession s : set) {
                    if (s.isOpen()) {
                        try { s.sendMessage(new TextMessage(text)); } catch (IOException ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
