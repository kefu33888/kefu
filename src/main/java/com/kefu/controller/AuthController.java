package com.kefu.controller;

import com.kefu.security.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String account = (String) body.get("account");
        String password = (String) body.get("password");
        if (account == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("code", 1, "msg", "account and password required"));
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account, password));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("code", 1, "msg", "登录失败: 账号或密码错误"));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("code", 1, "msg", "登录失败"));
        }

        String token = TokenStore.createTokenFor(account);
        Map<String, Object> user = new HashMap<>();
        user.put("account", account);
        if ("hxzc33".equals(account)) {
            user.put("roles", new String[]{"ADMIN"});
            user.put("balance", "999999");
        } else {
            user.put("roles", new String[]{"USER"});
            user.put("balance", "0");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return ResponseEntity.ok(Map.of("code", 0, "msg", "", "data", data));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("code", 1, "msg", "未登录"));
        }
        String token = auth.substring(7);
        String username = TokenStore.getUsername(token);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("code", 1, "msg", "未登录或 token 无效"));
        }

        Map<String, Object> user = new HashMap<>();
        user.put("account", username);
        if ("hxzc33".equals(username)) {
            user.put("roles", new String[]{"ADMIN"});
            user.put("balance", "999999");
        } else {
            user.put("roles", new String[]{"USER"});
            user.put("balance", "0");
        }

        return ResponseEntity.ok(Map.of("code", 0, "msg", "", "data", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            TokenStore.revoke(auth.substring(7));
        }
        return ResponseEntity.ok(Map.of("code", 0, "msg", "已登出"));
    }
}
