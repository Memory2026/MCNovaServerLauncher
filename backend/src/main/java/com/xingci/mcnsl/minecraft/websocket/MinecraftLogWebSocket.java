package com.xingci.mcnsl.minecraft.websocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Minecraft日志WebSocket
 *
 * 地址:
 *
 * ws://host:port/ws/minecraft/log/{id}
 *
 */
@Component
public class MinecraftLogWebSocket
        extends TextWebSocketHandler {



    /**
     * 实例ID -> WebSocket连接
     */
    private final Map<String, Set<WebSocketSession>> sessions =
            new ConcurrentHashMap<>();









    /**
     * 建立连接
     */
    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ){


        String id =
                getId(session);



        sessions
                .computeIfAbsent(
                        id,
                        k ->
                                ConcurrentHashMap
                                        .newKeySet()
                )
                .add(
                        session
                );


    }









    /**
     * 接收消息
     */
    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ){


        /*
         * 当前无需处理客户端消息
         */

    }









    /**
     * 关闭连接
     */
    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ){


        String id =
                getId(session);



        Set<WebSocketSession> list =
                sessions.get(id);



        if(list != null){


            list.remove(
                    session
            );


        }


    }









    /**
     * 推送日志
     *
     * Minecraft启动进程调用
     */
    public void send(
            String id,
            String log
    ){



        Set<WebSocketSession> list =
                sessions.get(id);





        if(list == null){


            return;


        }






        for(WebSocketSession session:list){



            try{


                if(session.isOpen()){


                    session.sendMessage(

                            new TextMessage(
                                    log
                            )

                    );


                }


            }
            catch(Exception ignored){


            }



        }



    }









    /**
     * 获取实例ID
     */
    private String getId(
            WebSocketSession session
    ){


        String path =
                session
                        .getUri()
                        .getPath();




        String[] split =
                path.split("/");




        return split[
                split.length-1
                ];

    }


}