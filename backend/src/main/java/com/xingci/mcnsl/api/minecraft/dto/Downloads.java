package com.xingci.mcnsl.api.minecraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Minecraft 下载信息集合
 */
@Data
public class Downloads {

    /**
     * 客户端
     */
    private DownloadInfo client;

    /**
     * 服务端
     */
    private DownloadInfo server;

    /**
     * Client Mappings
     */
    @JsonProperty("client_mappings")
    private DownloadInfo clientMappings;

    /**
     * Server Mappings
     */
    @JsonProperty("server_mappings")
    private DownloadInfo serverMappings;

}