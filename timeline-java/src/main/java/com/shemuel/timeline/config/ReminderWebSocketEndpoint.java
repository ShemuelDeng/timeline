package com.shemuel.timeline.config;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/reminder")   // ws://host/ws/reminder?token=xxx
public class ReminderWebSocketEndpoint {

    // userId -> 多个 session（用户可以打开多个 utools 窗口）
    private static final Map<Long, Set<Session>> USER_SESSIONS = new ConcurrentHashMap<>();

    private Long currentUserId;

    @OnOpen
    public void onOpen(Session session) {
        try {
            String query = session.getQueryString();   // token=xxxx
            if (query == null) {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "no token"));
                return;
            }

            String token = null;
            for (String kv : query.split("&")) {
                String[] arr = kv.split("=", 2);
                if (arr.length == 2 && "token".equals(arr[0])) {
                    token = URLDecoder.decode(arr[1], StandardCharsets.UTF_8.name());
                    break;
                }
            }
            if (token == null || token.isEmpty()) {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "no token"));
                return;
            }

            // 用 Sa-Token 解析登录用户
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "invalid token"));
                return;
            }

            currentUserId = Long.parseLong(loginId.toString());
            USER_SESSIONS
                    .computeIfAbsent(currentUserId, k -> ConcurrentHashMap.newKeySet())
                    .add(session);

            log.info("reminder ws connected, userId={}, sessionId={}", currentUserId, session.getId());
        } catch (Exception e) {
            log.error("ws onOpen error", e);
            try {
                session.close();
            } catch (IOException ignore) {
            }
        }
    }

    /** 新增：处理心跳等客户端消息 */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if (message == null) return;
            String text = message.trim();
            // 兼容纯文本 "PING"
            if ("PING".equalsIgnoreCase(text)) {
                session.getAsyncRemote().sendText("PONG");
                return;
            }

            // 兼容 JSON {"type":"PING"}
            JSONObject obj = JSON.parseObject(text);
            String type = obj.getString("type");
            if ("PING".equalsIgnoreCase(type)) {
                // 这里也用 JSON 回复，前端好识别
                session.getAsyncRemote().sendText("{\"type\":\"PONG\"}");
            }
        } catch (Exception e) {
            log.warn("ws onMessage parse error, msg={}", message, e);
        }
    }


    @OnClose
    public void onClose(Session session) {
        if (currentUserId != null) {
            Set<Session> sessions = USER_SESSIONS.get(currentUserId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    USER_SESSIONS.remove(currentUserId);
                }
            }
        }
        log.info("reminder ws closed, userId={}, sessionId={}", currentUserId, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        log.warn("reminder ws error, sessionId={}", session.getId(), thr);
    }

    // 对外暴露：给某个用户发消息
    public static void sendToUser(Long userId, Object payload) {
        Set<Session> sessions = USER_SESSIONS.get(userId);
        if (sessions == null || sessions.isEmpty()) return;

        String text = (payload instanceof String) ? (String) payload : JSON.toJSONString(payload);
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(text);
            }
        }
    }
}
