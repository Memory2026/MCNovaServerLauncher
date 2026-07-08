package com.xingci.mcnsl.config;

import com.xingci.mcnsl.websocket.SystemMonitorWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


/**
 * WebSocket 配置
 *
 * 地址：
 * ws://localhost:25565/ws/system
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    private final SystemMonitorWebSocketHandler handler;


    public WebSocketConfig(SystemMonitorWebSocketHandler handler) {
        this.handler = handler;
    }



    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry
    ) {

        registry
                .addHandler(handler, "/ws/system")
                .setAllowedOriginPatterns("*");

    }

}