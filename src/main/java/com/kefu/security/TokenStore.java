package com.kefu.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public final class TokenStore {
    private static final ConcurrentHashMap<String, String> TOKENS = new ConcurrentHashMap<>();

    private TokenStore() {}

    public static String createTokenFor(String username) {
        String token = UUID.randomUUID().toString();
        TOKENS.put(token, username);
        return token;
    }

    public static String getUsername(String token) {
        return TOKENS.get(token);
    }

    public static void revoke(String token) {
        TOKENS.remove(token);
    }
}
