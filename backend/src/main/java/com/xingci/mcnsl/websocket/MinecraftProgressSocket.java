package com.xingci.mcnsl.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.xingci.mcnsl.minecraft.download.MinecraftInstallTask;


import org.springframework.stereotype.Component;

import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.TextWebSocketHandler;



import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;



@Component
public class MinecraftProgressSocket
        extends TextWebSocketHandler {



    private final Set<WebSocketSession> sessions =
            new CopyOnWriteArraySet<>();


    private final ObjectMapper mapper =
            new ObjectMapper();







    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ){


        sessions.add(
                session
        );


    }









    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ){


        sessions.remove(
                session
        );


    }









    /**
     * 推送任务状态
     */
    public void send(
            MinecraftInstallTask task
    ){


        try{


            String json =

                    mapper.writeValueAsString(
                            task
                    );





            for(WebSocketSession session:sessions){



                if(session.isOpen()){


                    session.sendMessage(

                            new TextMessage(
                                    json
                            )

                    );


                }


            }


        }
        catch(Exception ignored){



        }



    }


}