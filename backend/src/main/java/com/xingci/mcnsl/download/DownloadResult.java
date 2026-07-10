package com.xingci.mcnsl.download;

import lombok.Data;

import java.nio.file.Path;
import java.time.Duration;

/**
 * 下载结果
 *
 * 描述一次下载任务最终执行结果。
 *
 * 用于：
 * - Minecraft版本下载
 * - Library下载
 * - Asset下载
 * - Mod下载
 * - 文件缓存检查
 */
@Data
public class DownloadResult {


    /**
     * 是否成功
     */
    private boolean success;


    /**
     * 对应下载任务
     */
    private DownloadTask task;


    /**
     * 下载完成后的文件
     */
    private Path file;


    /**
     * 文件大小(Byte)
     */
    private long size;


    /**
     * 下载耗时
     */
    private Duration duration;


    /**
     * 是否来自缓存
     *
     * true:
     * 文件已经存在，无需重新下载
     */
    private boolean cached;


    /**
     * 错误信息
     */
    private String error;



    public DownloadResult() {

    }



    /**
     * 创建成功结果
     */
    public static DownloadResult success(
            DownloadTask task,
            Path file,
            long size,
            Duration duration,
            boolean cached
    ){

        DownloadResult result =
                new DownloadResult();


        result.success = true;

        result.task = task;

        result.file = file;

        result.size = size;

        result.duration = duration;

        result.cached = cached;


        return result;
    }



    /**
     * 简化成功结果
     */
    public static DownloadResult success(
            DownloadTask task,
            Path file
    ){

        long size = 0;

        if(file != null){

            try {

                size =
                        java.nio.file.Files.size(file);

            } catch (Exception ignored){

            }
        }


        return success(
                task,
                file,
                size,
                null,
                false
        );
    }



    /**
     * 创建失败结果
     */
    public static DownloadResult failed(
            DownloadTask task,
            String error
    ){

        DownloadResult result =
                new DownloadResult();


        result.success = false;

        result.task = task;

        result.error = error;


        return result;
    }



    /**
     * 异常失败
     */
    public static DownloadResult failed(
            DownloadTask task,
            Exception exception
    ){

        return failed(
                task,
                exception.getMessage()
        );
    }



    /**
     * 是否下载成功
     */
    public boolean isSuccess(){

        return success;
    }



    /**
     * 是否下载失败
     */
    public boolean isFailed(){

        return !success;
    }



    /**
     * 是否命中缓存
     */
    public boolean isCached(){

        return cached;
    }

}