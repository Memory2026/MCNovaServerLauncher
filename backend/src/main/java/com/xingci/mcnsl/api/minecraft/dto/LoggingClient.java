package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

/**
 * 客户端日志配置
 */
@Data
public class LoggingClient {

    /**
     * 参数
     */
    private String argument;

    /**
     * 文件
     */
    private DownloadInfo file;

    /**
     * 类型
     */
    private String type;

}