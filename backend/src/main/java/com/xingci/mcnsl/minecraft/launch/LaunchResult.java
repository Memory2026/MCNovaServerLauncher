package com.xingci.mcnsl.minecraft.launch;


import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Minecraft启动结果
 *
 * 描述一次启动操作最终状态
 */
@Data
public class LaunchResult {



    /**
     * 是否成功启动
     */
    private boolean success;



    /**
     * Minecraft进程
     */
    private Process process;



    /**
     * 进程PID
     */
    private long pid;



    /**
     * 启动命令
     */
    private List<String> command;



    /**
     * 错误信息
     */
    private String error;



    /**
     * 创建时间
     */
    private LocalDateTime createTime;



    /**
     * 启动耗时
     */
    private Duration duration;



    /**
     * 默认构造
     */
    public LaunchResult(){

        this.createTime =
                LocalDateTime.now();

    }







    /**
     * 创建成功结果
     */
    public static LaunchResult success(
            Process process,
            List<String> command,
            Duration duration
    ){


        LaunchResult result =
                new LaunchResult();


        result.success =
                true;


        result.process =
                process;



        result.command =
                command;



        result.duration =
                duration;



        if(process != null){


            result.pid =
                    process.pid();


        }



        return result;


    }








    /**
     * 创建失败结果
     */
    public static LaunchResult failed(
            String error,
            Duration duration
    ){


        LaunchResult result =
                new LaunchResult();



        result.success =
                false;



        result.error =
                error;



        result.duration =
                duration;



        return result;


    }








    /**
     * 判断进程是否运行
     */
    public boolean isRunning(){


        return process != null
                &&
                process.isAlive();


    }





    /**
     * 停止进程
     */
    public void destroy(){


        if(process != null
                &&
                process.isAlive()){


            process.destroy();


        }


    }


}