package com.xingci.mcnsl.minecraft.download;


import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Duration;


/**
 * Minecraft 安装结果
 *
 * 描述一次 Minecraft 安装完成后的状态
 */
@Data
public class MinecraftInstallResult {


    /**
     * Minecraft版本
     *
     * 例如：
     *
     * 1.21.8
     */
    private String version;



    /**
     * 游戏目录
     */
    private Path gameDir;



    /**
     * 客户端 jar
     */
    private Path clientJar;



    /**
     * 是否成功
     */
    private boolean success;



    /**
     * 安装开始时间
     */
    private LocalDateTime startTime;



    /**
     * 安装结束时间
     */
    private LocalDateTime finishTime;



    /**
     * 安装耗时
     */
    private Duration duration;



    /**
     * 错误信息
     */
    private String error;



    public MinecraftInstallResult(){

    }





    /**
     * 成功结果
     */
    public MinecraftInstallResult(
            String version,
            Path gameDir,
            Path clientJar
    ){

        this.version =
                version;

        this.gameDir =
                gameDir;

        this.clientJar =
                clientJar;

        this.success =
                true;

        this.finishTime =
                LocalDateTime.now();

    }





    /**
     * 创建失败结果
     */
    public static MinecraftInstallResult failed(
            String version,
            Path gameDir,
            String error
    ){

        MinecraftInstallResult result =
                new MinecraftInstallResult();


        result.version =
                version;


        result.gameDir =
                gameDir;


        result.success =
                false;


        result.error =
                error;


        result.finishTime =
                LocalDateTime.now();


        return result;

    }




    /**
     * 设置耗时
     */
    public void finish(
            LocalDateTime start
    ){

        this.startTime =
                start;


        this.finishTime =
                LocalDateTime.now();


        this.duration =
                Duration.between(
                        startTime,
                        finishTime
                );

    }


}