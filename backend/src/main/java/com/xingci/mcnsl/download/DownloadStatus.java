package com.xingci.mcnsl.download;


/**
 * 下载任务状态
 */
public enum DownloadStatus {


    /**
     * 等待下载
     */
    PENDING,


    /**
     * 下载中
     *
     * 兼容旧代码
     */
    RUNNING,


    /**
     * 下载中(推荐新名称)
     */
    DOWNLOADING,


    /**
     * 下载完成
     */
    COMPLETED,


    /**
     * 下载失败
     */
    FAILED,


    /**
     * 已取消
     */
    CANCELLED


}