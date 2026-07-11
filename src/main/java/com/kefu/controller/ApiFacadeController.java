package com.kefu.controller;

import com.kefu.security.TokenStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiFacadeController {

    private final Map<Long, Map<String, Object>> users = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> payments = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> recharges = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> announcements = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> redEnvelopes = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> apiLinks = new ConcurrentHashMap<>();
    private final Map<String, Object> settings = new ConcurrentHashMap<>();
    private final AtomicLong userSequence = new AtomicLong(1001);
    private final AtomicLong apiLinkSequence = new AtomicLong(1);

    public ApiFacadeController() {
        seedUsers();
        seedAnnouncements();
        seedRedEnvelopes();
        seedLinks();
        settings.put("tgUsername", "@hxzcsansan");
        settings.put("tgTitle", "客服系统");
        settings.put("tgDesc1", "请联系唯一飞机购买，不在我本人这里购买的无售后");
        settings.put("tgDesc2", "频道 @hxzcsansan");
    }

    private void seedUsers() {
        Map<String, Object> admin = new LinkedHashMap<>();
        admin.put("id", 1L);
        admin.put("account", "hxzc33");
        admin.put("role", "admin");
        admin.put("balance", 999999D);
        admin.put("expireAt", null);
        admin.put("expireDays", -1);
        admin.put("permissions", "user:create,user:read,user:update,user:delete,api:create,api:read,api:update,api:delete");
        admin.put("createdBy", "system");
        users.put(1L, admin);
    }

    private void seedAnnouncements() {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", 1L);
        item.put("title", "系统已恢复");
        item.put("content", "客服管理后台接口已恢复，欢迎继续使用。");
        item.put("active", true);
        announcements.put("1", item);
    }

    private void seedRedEnvelopes() {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", 1L);
        item.put("name", "首单福利");
        item.put("amount", 10D);
        item.put("status", "active");
        redEnvelopes.put("1", item);
    }

    private void seedLinks() {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", 1L);
        item.put("name", "默认接口");
        item.put("url", "https://example.com/api");
        item.put("price", 0D);
        item.put("paid", false);
        item.put("status", "active");
        apiLinks.put("1", item);
    }

    private ResponseEntity<Map<String, Object>> ok(Object data) {
        return ResponseEntity.ok(Map.of("code", 0, "msg", "", "data", data));
    }

    private ResponseEntity<Map<String, Object>> fail(String msg, int status) {
        return ResponseEntity.status(status).body(Map.of("code", 1, "msg", msg, "data", null));
    }

    private String currentUsername(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        return TokenStore.getUsername(auth.substring(7));
    }

    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> userInfo(@RequestHeader(value = "Authorization", required = false) String auth) {
        String username = currentUsername(auth);
        if (username == null) {
            return fail("未登录", 401);
        }
        return ok(Map.of(
                "account", username,
                "role", "hxzc33".equals(username) ? "admin" : "user",
                "balance", "hxzc33".equals(username) ? 999999D : 0D,
                "expireDate", "永久有效",
                "expireDays", "hxzc33".equals(username) ? 365000 : 30,
                "isExpired", false,
                "canUseChat", true,
                "permissions", "hxzc33".equals(username) ? "user:create,user:read,user:update,user:delete,api:create,api:read,api:update,api:delete" : ""
        ));
    }

    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> settings() {
        return ok(settings);
    }

    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> body) {
        settings.putAll(body);
        return ok(settings);
    }

    @GetMapping("/settings/public/tg_username")
    public ResponseEntity<Map<String, Object>> tgUsername() {
        return ok(Map.of("tgUsername", settings.getOrDefault("tgUsername", "@hxzcsansan")));
    }

    @GetMapping({"/announcement", "/announcement/list", "/announcement/active"})
    public ResponseEntity<Map<String, Object>> announcements(@RequestParam(value = "active", required = false) Boolean active) {
        List<Map<String, Object>> result = new ArrayList<>(announcements.values());
        if (active != null) {
            result = result.stream().filter(item -> Boolean.TRUE.equals(item.get("active"))).collect(Collectors.toList());
        }
        return ok(result);
    }

    @PostMapping("/announcement")
    public ResponseEntity<Map<String, Object>> createAnnouncement(@RequestBody Map<String, Object> body) {
        String id = String.valueOf(System.currentTimeMillis());
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("title", body.getOrDefault("title", "公告"));
        item.put("content", body.getOrDefault("content", ""));
        item.put("active", Boolean.TRUE.equals(body.get("active")) || body.get("active") == null);
        announcements.put(id, item);
        return ok(item);
    }

    @GetMapping("/red-envelope")
    public ResponseEntity<Map<String, Object>> redEnvelopeList() {
        return ok(new ArrayList<>(redEnvelopes.values()));
    }

    @GetMapping("/red-envelope/list")
    public ResponseEntity<Map<String, Object>> redEnvelopeListAlias() {
        return redEnvelopeList();
    }

    @GetMapping("/red-envelope/active")
    public ResponseEntity<Map<String, Object>> activeRedEnvelope() {
        return ok(redEnvelopes.values().stream().filter(item -> "active".equals(item.get("status"))).findFirst().orElseGet(LinkedHashMap::new));
    }

    @PostMapping("/red-envelope/claim/{id}")
    public ResponseEntity<Map<String, Object>> claimRedEnvelope(@PathVariable String id) {
        Map<String, Object> item = redEnvelopes.get(id);
        if (item == null) {
            return fail("红包不存在", 404);
        }
        item.put("claimed", true);
        return ok(item);
    }

    @PostMapping("/payment/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> body) {
        String orderNumber = String.valueOf(body.getOrDefault("orderNumber", UUID.randomUUID()));
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("orderNumber", orderNumber);
        item.put("amount", body.getOrDefault("amount", 0));
        item.put("apiLink", body.getOrDefault("apiLink", ""));
        item.put("paymentMethod", body.getOrDefault("paymentMethod", "支付宝"));
        item.put("expireTime", body.getOrDefault("expireTime", System.currentTimeMillis() + 1800000));
        item.put("isExpired", false);
        item.put("remainingTime", "30分钟");
        payments.put(orderNumber, item);
        return ok(item);
    }

    @GetMapping("/payment/list")
    public ResponseEntity<Map<String, Object>> listPayments() {
        return ok(new ArrayList<>(payments.values()));
    }

    @PutMapping("/payment/update/{orderNumber}")
    public ResponseEntity<Map<String, Object>> updatePayment(@PathVariable String orderNumber, @RequestBody Map<String, Object> body) {
        Map<String, Object> item = payments.computeIfAbsent(orderNumber, key -> new LinkedHashMap<>());
        item.putAll(body);
        item.put("orderNumber", orderNumber);
        payments.put(orderNumber, item);
        return ok(item);
    }

    @GetMapping("/payment/order/{orderNumber}")
    public ResponseEntity<Map<String, Object>> paymentOrder(@PathVariable String orderNumber) {
        Map<String, Object> item = payments.get(orderNumber);
        return item == null ? fail("订单不存在", 404) : ok(item);
    }

    @PostMapping("/recharge/create")
    public ResponseEntity<Map<String, Object>> createRecharge(@RequestBody Map<String, Object> body) {
        String orderId = "R" + System.currentTimeMillis();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("orderId", orderId);
        item.put("address", "TQ4h5Y8fDv3QxGhaP6Tgq3mQ8dVJb7tA4p");
        item.put("usdtAmount", body.getOrDefault("usdtAmount", 1));
        item.put("status", "pending");
        recharges.put(orderId, item);
        return ok(item);
    }

    @GetMapping("/recharge/status/{orderId}")
    public ResponseEntity<Map<String, Object>> rechargeStatus(@PathVariable String orderId) {
        Map<String, Object> item = recharges.get(orderId);
        return item == null ? fail("充值订单不存在", 404) : ok(item);
    }

    @GetMapping("/recharge/config")
    public ResponseEntity<Map<String, Object>> rechargeConfig() {
        return ok(Map.of("minAmount", 1, "network", "TRC20", "note", "请先创建订单后再汇款"));
    }

    @GetMapping("/recharge/admin/list")
    public ResponseEntity<Map<String, Object>> adminRechargeList() {
        return ok(new ArrayList<>(recharges.values()));
    }

    @PostMapping("/recharge/admin/confirm/{orderId}")
    public ResponseEntity<Map<String, Object>> confirmRecharge(@PathVariable String orderId) {
        Map<String, Object> item = recharges.get(orderId);
        if (item == null) {
            return fail("充值订单不存在", 404);
        }
        item.put("status", "paid");
        return ok(item);
    }

    @PostMapping("/recharge/admin/cancel/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelRecharge(@PathVariable String orderId) {
        Map<String, Object> item = recharges.get(orderId);
        if (item == null) {
            return fail("充值订单不存在", 404);
        }
        item.put("status", "cancelled");
        return ok(item);
    }

    @PostMapping("/recharge/admin/verify-huobi")
    public ResponseEntity<Map<String, Object>> verifyHuobi() {
        return ok(Map.of("verified", true));
    }

    @GetMapping("/admin/users/my-permissions")
    public ResponseEntity<Map<String, Object>> myPermissions() {
        return ok(Map.of(
                "isAdmin", true,
                "permissions", "user:create,user:read,user:update,user:delete,api:create,api:read,api:update,api:delete"
        ));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Map<String, Object>> adminUsers() {
        return ok(new ArrayList<>(users.values()));
    }

    @PostMapping("/admin/users")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, Object> body) {
        Long id = userSequence.incrementAndGet();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("account", body.getOrDefault("account", ""));
        item.put("role", body.getOrDefault("role", "user"));
        item.put("balance", body.getOrDefault("balance", 0));
        item.put("expireAt", body.get("expireAt"));
        item.put("expireDays", body.getOrDefault("expireDays", 30));
        item.put("permissions", body.getOrDefault("permissions", ""));
        item.put("createdBy", "admin");
        users.put(id, item);
        return ok(item);
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> item = users.get(id);
        if (item == null) {
            return fail("用户不存在", 404);
        }
        item.putAll(body);
        users.put(id, item);
        return ok(item);
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> removed = users.remove(id);
        return removed == null ? fail("用户不存在", 404) : ok(Map.of("deleted", true));
    }

    @PutMapping("/admin/users/{id}/permissions")
    public ResponseEntity<Map<String, Object>> updatePermissions(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> item = users.get(id);
        if (item == null) {
            return fail("用户不存在", 404);
        }
        item.put("permissions", body.getOrDefault("permissions", ""));
        users.put(id, item);
        return ok(item);
    }

    @GetMapping("/admin/api-links")
    public ResponseEntity<Map<String, Object>> adminApiLinks() {
        return ok(new ArrayList<>(apiLinks.values()));
    }

    @PostMapping("/admin/api-links")
    public ResponseEntity<Map<String, Object>> createApiLink(@RequestBody Map<String, Object> body) {
        Long id = apiLinkSequence.incrementAndGet();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("name", body.getOrDefault("name", "接口"));
        item.put("url", body.getOrDefault("url", ""));
        item.put("price", body.getOrDefault("price", 0));
        item.put("status", body.getOrDefault("status", "active"));
        item.put("paid", body.getOrDefault("paid", false));
        apiLinks.put(String.valueOf(id), item);
        return ok(item);
    }

    @PostMapping("/admin/api-links/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteApiLinks(@RequestBody Map<String, Object> body) {
        List<?> ids = (List<?>) body.getOrDefault("ids", List.of());
        int deletedCount = 0;
        for (Object id : ids) {
            if (apiLinks.remove(String.valueOf(id)) != null) {
                deletedCount++;
            }
        }
        return ok(Map.of("deletedCount", deletedCount));
    }

    @PostMapping("/admin/api-links/batch-refund")
    public ResponseEntity<Map<String, Object>> batchRefundApiLinks(@RequestBody Map<String, Object> body) {
        return ok(Map.of("refundedCount", 0));
    }

    @GetMapping("/user/api-links/available")
    public ResponseEntity<Map<String, Object>> availableLinks() {
        return ok(new ArrayList<>(apiLinks.values()));
    }

    @GetMapping("/user/api-links/purchased")
    public ResponseEntity<Map<String, Object>> purchasedLinks() {
        return ok(new ArrayList<>(apiLinks.values()));
    }

    @GetMapping("/user/api-links/paid-price")
    public ResponseEntity<Map<String, Object>> paidPrice() {
        return ok(Map.of("price", 0));
    }

    @PostMapping("/user/api-links/purchase/{id}")
    public ResponseEntity<Map<String, Object>> purchaseLink(@PathVariable String id) {
        Map<String, Object> item = apiLinks.get(id);
        if (item == null) {
            return fail("接口不存在", 404);
        }
        item.put("paid", true);
        return ok(item);
    }

    @PostMapping("/user/api-links/apply/{id}")
    public ResponseEntity<Map<String, Object>> applyLink(@PathVariable String id) {
        return purchaseLink(id);
    }

    @PostMapping("/user/api-links/cancel/{id}")
    public ResponseEntity<Map<String, Object>> cancelLink(@PathVariable String id) {
        Map<String, Object> item = apiLinks.get(id);
        if (item == null) {
            return fail("接口不存在", 404);
        }
        item.put("paid", false);
        return ok(item);
    }

    @PostMapping("/user/api-links/toggle")
    public ResponseEntity<Map<String, Object>> toggleLink(@RequestBody Map<String, Object> body) {
        return ok(body);
    }

    @GetMapping("/admin/configs")
    public ResponseEntity<Map<String, Object>> adminConfigs() {
        return ok(Map.of("mode", "demo", "status", "ok"));
    }

    @GetMapping("/admin/domains")
    public ResponseEntity<Map<String, Object>> adminDomains() {
        return ok(List.of(Map.of("name", "默认域名", "url", "https://example.com", "qrDataUrl", "")));
    }

    @GetMapping("/admin/monitoring/dashboard")
    public ResponseEntity<Map<String, Object>> monitoringDashboard() {
        return ok(Map.of("users", users.size(), "orders", payments.size(), "recharges", recharges.size()));
    }

    @PostMapping("/admin/test-oss")
    public ResponseEntity<Map<String, Object>> testOss() {
        return ok(Map.of("success", true));
    }

    @PostMapping("/admin/upload")
    public ResponseEntity<Map<String, Object>> adminUpload() {
        return ok(Map.of("url", "/img/default.png"));
    }

    @PostMapping("/license/add")
    public ResponseEntity<Map<String, Object>> addLicense() {
        return ok(Map.of("created", true));
    }

    @GetMapping("/license/list")
    public ResponseEntity<Map<String, Object>> licenseList() {
        return ok(List.of(Map.of("id", 1, "name", "默认授权")));
    }

    @PutMapping("/license/update/{id}")
    public ResponseEntity<Map<String, Object>> updateLicense(@PathVariable String id) {
        return ok(Map.of("id", id));
    }

    @DeleteMapping("/license/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteLicense(@PathVariable String id) {
        return ok(Map.of("deleted", true));
    }

    @PostMapping("/license/revoke/{id}")
    public ResponseEntity<Map<String, Object>> revokeLicense(@PathVariable String id) {
        return ok(Map.of("revoked", true));
    }

    @PostMapping("/order/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        return ok(Map.of("orderId", UUID.randomUUID().toString(), "status", "created"));
    }

    @PostMapping("/order/upload-image")
    public ResponseEntity<Map<String, Object>> uploadOrderImage() {
        return ok(Map.of("url", "/img/default.png"));
    }

    @GetMapping("/order/image/{id}")
    public ResponseEntity<Map<String, Object>> orderImage(@PathVariable String id) {
        return ok(Map.of("id", id, "url", "/img/default.png"));
    }

    @GetMapping("/chat/agent-status")
    public ResponseEntity<Map<String, Object>> chatAgentStatus() {
        return ok(Map.of("online", true));
    }

    @GetMapping("/chat/online-status")
    public ResponseEntity<Map<String, Object>> chatOnlineStatus() {
        return ok(Map.of("online", true));
    }

    @GetMapping("/chat/poll")
    public ResponseEntity<Map<String, Object>> chatPoll() {
        return ok(List.of());
    }

    @PostMapping("/chat/send")
    public ResponseEntity<Map<String, Object>> chatSend() {
        return ok(Map.of("sent", true));
    }

    @GetMapping("/chat/sessions")
    public ResponseEntity<Map<String, Object>> chatSessions() {
        return ok(List.of());
    }

    @GetMapping("/chat/messages")
    public ResponseEntity<Map<String, Object>> chatMessages() {
        return ok(List.of());
    }

    @PostMapping("/chat/register-visit")
    public ResponseEntity<Map<String, Object>> registerVisit() {
        return ok(Map.of("ok", true));
    }

    @PostMapping("/chat/track-visit")
    public ResponseEntity<Map<String, Object>> trackVisit() {
        return ok(Map.of("ok", true));
    }

    @PostMapping("/push/subscribe")
    public ResponseEntity<Map<String, Object>> subscribePush() {
        return ok(Map.of("ok", true));
    }

    @PostMapping("/push/unsubscribe")
    public ResponseEntity<Map<String, Object>> unsubscribePush() {
        return ok(Map.of("ok", true));
    }

    @GetMapping("/push/vapid-key")
    public ResponseEntity<Map<String, Object>> vapidKey() {
        return ok(Map.of("key", "demo"));
    }
}
