package com.xingci.mcnsl.task;

import com.xingci.mcnsl.websocket.SystemMonitorWebSocketHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 系统监控定时推送任务
 */
@Component
public class SystemMonitorTask {


    private final SystemMonitorWebSocketHandler handler;


    public SystemMonitorTask(
            SystemMonitorWebSocketHandler handler
    ) {
        this.handler = handler;
    }



    /**
     * 每秒推送一次系统数据
     */
    @Scheduled(fixedRate = 1000)
    public void pushSystemInfo() {

        handler.sendSystemInfo();

    }

}