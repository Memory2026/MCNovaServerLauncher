package com.xingci.mcnsl.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingci.mcnsl.manager.SystemMonitorManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * 系统监控 WebSocket 处理器
 *
 * 负责：
 * 1. 管理连接
 * 2. 推送 CPU / 内存 / 磁盘数据
 */
@Component
public class SystemMonitorWebSocketHandler
        extends TextWebSocketHandler {


    private final SystemMonitorManager monitorManager;


    private final ObjectMapper objectMapper =
            new ObjectMapper();



    /**
     * 当前在线客户端
     */
    private final Set<WebSocketSession> sessions =
            new CopyOnWriteArraySet<>();



    public SystemMonitorWebSocketHandler(
            SystemMonitorManager monitorManager
    ) {

        this.monitorManager = monitorManager;

    }



    /**
     * 新连接建立
     */
    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) {

        sessions.add(session);

        System.out.println(
                "[SystemMonitor] WebSocket连接:"
                        + session.getId()
        );

    }




    /**
     * 连接关闭
     */
    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {

        sessions.remove(session);

        System.out.println(
                "[SystemMonitor] WebSocket断开:"
                        + session.getId()
        );

    }



    /**
     * 推送系统数据
     */
    public void sendSystemInfo() {


        try {


            String json =
                    objectMapper.writeValueAsString(
                            monitorManager.getSystemInfo()
                    );



            TextMessage message =
                    new TextMessage(json);



            for (WebSocketSession session : sessions) {


                if (session.isOpen()) {


                    session.sendMessage(message);


                }

            }


        } catch (Exception e) {


            e.printStackTrace();


        }

    }

}