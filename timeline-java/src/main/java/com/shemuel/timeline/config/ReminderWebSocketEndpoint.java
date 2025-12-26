package com.shemuel.timeline.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.EOFException;
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

    private static final CloseReason.CloseCode CODE_NO_TOKEN = () -> 4000;
    private static final CloseReason.CloseCode CODE_TOKEN_TIMEOUT = () -> 4001;
    private static final CloseReason.CloseCode CODE_TOKEN_INVALID = () -> 4002;

    private static CloseReason close(int code, String reason) {
        return new CloseReason(() -> code, reason);
    }
    // userId -> 多个 session（用户可以打开多个 utools 窗口）
    private static final Map<Long, Set<Session>> USER_SESSIONS = new ConcurrentHashMap<>();

    private Long currentUserId;

    @OnOpen
    public void onOpen(Session session) {
        try {
            String query = session.getQueryString();   // token=xxxx
            if (query == null) {
                session.close(close(4000, "NO_TOKEN"));
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
                session.close(close(4000, "NO_TOKEN"));
                return;
            }

            // 存一下 token，后面心跳可复验（可选，但很实用）
            session.getUserProperties().put("token", token);

            Object loginId;
            try {
                loginId = StpUtil.getLoginIdByToken(token);
            } catch (NotLoginException e) {
                // 这里能区分很多类型：TOKEN_TIMEOUT / INVALID_TOKEN / BE_REPLACED / KICK_OUT ...
                String type = e.getType();
                if (NotLoginException.TOKEN_TIMEOUT.equals(type)) {
                    session.close(close(4001, "TOKEN_TIMEOUT"));
                } else {
                    session.close(close(4002, "TOKEN_INVALID:" + type));
                }
                return;
            }

            if (loginId == null) {
                session.close(close(4002, "TOKEN_INVALID"));
                return;
            }

            currentUserId = Long.parseLong(loginId.toString());
            USER_SESSIONS.computeIfAbsent(currentUserId, k -> ConcurrentHashMap.newKeySet()).add(session);

            log.info("reminder ws connected, userId={}, sessionId={}", currentUserId, session.getId());
        } catch (Exception e) {
            log.error("ws onOpen error", e);
            try { session.close(); } catch (IOException ignore) {}
        }
    }

    /** 新增：处理心跳等客户端消息 */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if (message == null) return;
            String text = message.trim();

            boolean isPing = "PING".equalsIgnoreCase(text);
            if (!isPing) {
                try {
                    JSONObject obj = JSON.parseObject(text);
                    isPing = "PING".equalsIgnoreCase(obj.getString("type"));
                } catch (Exception ignore) {}
            }

            if (!isPing) return;

            String token = (String) session.getUserProperties().get("token");
            if (token != null) {
                try {
                    Object loginId = StpUtil.getLoginIdByToken(token);
                    if (loginId == null) {
                        session.close(close(4002, "TOKEN_INVALID"));
                        return;
                    }
                } catch (NotLoginException e) {
                    if (NotLoginException.TOKEN_TIMEOUT.equals(e.getType())) {
                        session.close(close(4001, "TOKEN_TIMEOUT"));
                    } else {
                        session.close(close(4002, "TOKEN_INVALID:" + e.getType()));
                    }
                    return;
                }
            }

            session.getAsyncRemote().sendText("{\"type\":\"PONG\"}");
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
    public void onError(Session session, Throwable t) {
        String sid = session != null ? session.getId() : "null";

        if (isClientDisconnect(t)) {
            // 这类就是：客户端/代理断开了，属于正常波动
            log.info("reminder ws closed by peer, sessionId={}, reason={}", sid, t.toString());
            safeClose(session);
            return;
        }

        log.error("reminder ws error, sessionId={}", sid, t);
        safeClose(session);
    }

    private boolean isClientDisconnect(Throwable t) {
        if (t instanceof EOFException) return true;

        // 有时 EOF 包在 IOException / RuntimeException 里
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof EOFException) return true;
            if (cur instanceof IOException) {
                String msg = cur.getMessage();
                if (msg != null) {
                    String m = msg.toLowerCase();
                    if (m.contains("broken pipe") || m.contains("connection reset") || m.contains("reset by peer")) {
                        return true;
                    }
                }
            }
            cur = cur.getCause();
        }
        return false;
    }

    private void safeClose(Session session) {
        if (session == null) return;
        try {
            if (session.isOpen()) session.close();
        } catch (Exception ignored) {}
    }

    // 对外暴露：给某个用户发消息
    public static void sendToUser(Long userId, Object payload) {
        Set<Session> sessions = USER_SESSIONS.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            log.info("推送提醒任务时，用户ws已经断开{}", userId);
            return;
        }

        String text = (payload instanceof String) ? (String) payload : JSON.toJSONString(payload);
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(text);
            }
        }
    }
}
