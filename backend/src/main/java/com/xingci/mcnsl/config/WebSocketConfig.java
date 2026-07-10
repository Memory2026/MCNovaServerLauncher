package com.xingci.mcnsl.config;


import com.xingci.mcnsl.minecraft.websocket.MinecraftLogWebSocket;
import com.xingci.mcnsl.websocket.SystemMonitorWebSocketHandler;
import com.xingci.mcnsl.websocket.MinecraftProgressSocket;


import org.springframework.context.annotation.Configuration;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;



/**
 * WebSocket 配置
 *
 * 系统监控:
 *
 * ws://localhost:25565/ws/system
 *
 *
 * Minecraft日志:
 *
 * ws://localhost:25565/ws/minecraft/log?id=xxx
 *
 *
 * Minecraft安装进度:
 *
 * ws://localhost:25565/ws/minecraft/install
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig
        implements WebSocketConfigurer {



    private final SystemMonitorWebSocketHandler systemHandler;


    private final MinecraftLogWebSocket minecraftLogHandler;


    private final MinecraftProgressSocket progressSocket;







    public WebSocketConfig(
            SystemMonitorWebSocketHandler systemHandler,
            MinecraftLogWebSocket minecraftLogHandler,
            MinecraftProgressSocket progressSocket
    ){


        this.systemHandler =
                systemHandler;



        this.minecraftLogHandler =
                minecraftLogHandler;



        this.progressSocket =
                progressSocket;


    }









    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry
    ){



        /*
         * 系统监控
         */
        registry

                .addHandler(
                        systemHandler,
                        "/ws/system"
                )

                .setAllowedOriginPatterns(
                        "*"
                );








        /*
         * Minecraft服务器日志
         */
        registry

                .addHandler(
                        minecraftLogHandler,
                        "/ws/minecraft/log"
                )

                .setAllowedOriginPatterns(
                        "*"
                );









        /*
         * Minecraft客户端安装进度
         */
        registry

                .addHandler(
                        progressSocket,
                        "/ws/minecraft/install"
                )

                .setAllowedOriginPatterns(
                        "*"
                );


    }


}