package com.xingci.mcnsl.minecraft.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MinecraftLogWebSocket
        extends TextWebSocketHandler {

    private final Map<String, Set<WebSocketSession>> sessions =
            new ConcurrentHashMap<>();

    private final Set<WebSocketSession> globalSessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ){

        String path = session.getUri().getPath();

        if (path.equals("/ws/minecraft/logs")) {
            globalSessions.add(session);
            System.out.println("[MinecraftLogWebSocket] 全局订阅连接: " + session.getId());
        } else {
            String id = getId(session);
            sessions
                    .computeIfAbsent(
                            id,
                            k -> ConcurrentHashMap.newKeySet()
                    )
                    .add(session);
            System.out.println("[MinecraftLogWebSocket] 实例订阅连接: " + id + " - " + session.getId());
        }

    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ){
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ){

        globalSessions.remove(session);

        for (Set<WebSocketSession> sessionSet : sessions.values()) {
            sessionSet.remove(session);
        }

    }

    public void send(
            String id,
            String log
    ){

        Set<WebSocketSession> list = sessions.get(id);

        if (list != null) {
            for (WebSocketSession session : list) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(log));
                    }
                } catch (Exception ignored) {
                }
            }
        }

        for (WebSocketSession session : globalSessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(log));
                }
            } catch (Exception ignored) {
            }
        }

    }

    private String getId(
            WebSocketSession session
    ){

        String path = session.getUri().getPath();
        String[] split = path.split("/");

        if (split.length >= 5) {
            return split[4];
        }
        return "default";

    }

}