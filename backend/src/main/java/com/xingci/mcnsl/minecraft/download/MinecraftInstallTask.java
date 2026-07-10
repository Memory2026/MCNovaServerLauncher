package com.xingci.mcnsl.minecraft.download;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;



/**
 * Minecraft安装任务
 */
@Data
public class MinecraftInstallTask {



    private String id =
            UUID.randomUUID().toString();



    private String version;



    private InstallStatus status =
            InstallStatus.WAITING;



    private String step;



    private String currentFile;



    private long downloadedBytes;



    private long totalBytes;



    private double progress;



    private long speed;



    private String error;



    private LocalDateTime createTime =
            LocalDateTime.now();



    private LocalDateTime finishTime;



    /**
     * 状态监听
     */
    private transient Runnable listener;







    public void setListener(
            Runnable listener
    ){

        this.listener = listener;

    }









    public synchronized void start(){

        status =
                InstallStatus.RUNNING;

        notifyChange();

    }









    public synchronized void update(
            long current,
            long total
    ){


        downloadedBytes =
                current;


        totalBytes =
                total;



        if(total > 0){

            progress =
                    current * 100.0 / total;

        }


        notifyChange();

    }









    public void setStep(
            String step
    ){

        this.step =
                step;


        notifyChange();

    }









    public void setCurrentFile(
            String file
    ){

        this.currentFile =
                file;


        notifyChange();

    }









    public void success(){

        status =
                InstallStatus.SUCCESS;


        progress =
                100;


        finishTime =
                LocalDateTime.now();



        notifyChange();

    }









    public void failed(
            String message
    ){

        status =
                InstallStatus.FAILED;


        error =
                message;


        finishTime =
                LocalDateTime.now();



        notifyChange();

    }









    private void notifyChange(){

        if(listener!=null){

            listener.run();

        }

    }


}