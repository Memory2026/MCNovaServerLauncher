package com.xingci.mcnsl.minecraft.download;


/**
 * Minecraft安装状态
 */
public enum InstallStatus {


    /**
     * 等待
     */
    WAITING,


    /**
     * 运行中
     */
    RUNNING,


    /**
     * 下载中
     */
    DOWNLOADING,


    /**
     * 安装中
     */
    INSTALLING,


    /**
     * 成功
     */
    SUCCESS,


    /**
     * 失败
     */
    FAILED,


    /**
     * 取消
     */
    CANCELLED

}