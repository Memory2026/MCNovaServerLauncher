package com.xingci.mcnsl.download;


import lombok.Getter;
import lombok.Setter;


import java.nio.file.Path;
import java.util.UUID;


/**
 * 下载任务
 *
 * 统一管理：
 *
 * DownloadManager
 * DownloadWorker
 * FileDownloader
 * HttpDownloader
 *
 */
@Getter
@Setter
public class DownloadTask {



    /**
     * 任务ID
     */
    private final String id =
            UUID.randomUUID()
                    .toString();




    /**
     * 任务名称
     */
    private final String name;





    /**
     * 下载请求
     */
    private DownloadRequest request;





    /**
     * 当前下载文件
     */
    private String currentFile;





    /**
     * 当前步骤
     *
     * 例如:
     *
     * 等待
     * 连接服务器
     * 下载中
     * 校验文件
     * 完成
     */
    private String step =
            "等待";





    /**
     * 下载地址
     */
    private String url;





    /**
     * 保存路径
     */
    private Path target;





    /**
     * 文件大小
     */
    private long totalBytes;





    /**
     * 已下载大小
     */
    private long downloadedBytes;





    /**
     * 下载速度
     */
    private long speed;





    /**
     * 下载状态
     */
    private DownloadStatus status =
            DownloadStatus.PENDING;





    /**
     * 错误信息
     */
    private String error;





    /**
     * 创建时间
     */
    private final long createdTime =
            System.currentTimeMillis();





    /**
     * 开始时间
     */
    private long startTime;





    /**
     * 完成时间
     */
    private long finishedTime;








    public DownloadTask(
            String name
    ){

        this.name =
                name;

    }









    /**
     * 开始下载
     */
    public synchronized void start(){


        status =
                DownloadStatus.RUNNING;


        step =
                "开始下载";


        startTime =
                System.currentTimeMillis();


    }









    /**
     * 更新当前文件
     */
    public void updateCurrentFile(
            String file
    ){

        this.currentFile =
                file;

    }









    /**
     * 更新步骤
     */
    public void updateStep(
            String step
    ){

        this.step =
                step;

    }









    /**
     * 更新下载进度
     */
    public synchronized void update(
            long bytes
    ){

        this.downloadedBytes =
                bytes;

    }









    /**
     * 更新下载进度和速度
     *
     * DownloadWorker使用
     */
    public synchronized void update(
            long bytes,
            long speed
    ){

        this.downloadedBytes =
                bytes;


        this.speed =
                speed;


    }









    /**
     * 更新速度
     */
    public void updateSpeed(
            long speed
    ){

        this.speed =
                speed;

    }









    /**
     * 无参数完成
     */
    public synchronized void finish(){


        status =
                DownloadStatus.COMPLETED;


        step =
                "完成";


        finishedTime =
                System.currentTimeMillis();


    }









    /**
     * 带结果完成
     */
    public synchronized void finish(
            boolean success,
            String message
    ){


        if(success){


            status =
                    DownloadStatus.COMPLETED;


            step =
                    "完成";


        }
        else{


            status =
                    DownloadStatus.FAILED;


            step =
                    "失败";


            error =
                    message;


        }



        finishedTime =
                System.currentTimeMillis();


    }









    /**
     * 完成
     */
    public void complete(){


        finish();


    }









    /**
     * 失败
     */
    public void fail(
            String message
    ){


        finish(
                false,
                message
        );


    }









    /**
     * 取消
     */
    public synchronized void cancel(){


        status =
                DownloadStatus.CANCELLED;


        step =
                "取消";


    }









    /**
     * 是否运行
     */
    public boolean isRunning(){


        return status ==
                DownloadStatus.RUNNING

                ||

                status ==
                        DownloadStatus.DOWNLOADING;


    }









    /**
     * 下载百分比
     */
    public double getProgress(){


        if(totalBytes <= 0){

            return 0;

        }


        return downloadedBytes
                *100.0
                /
                totalBytes;


    }









    /**
     * 获取速度
     *
     * 兼容旧代码
     */
    public long getIdSpeed(){


        return speed;


    }


}