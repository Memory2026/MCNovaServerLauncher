package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

/**
 * 下载信息
 *
 * Mojang JSON 中大量使用该结构：
 *
 * {
 *   "sha1": "...",
 *   "size": 123456,
 *   "url": "https://..."
 * }
 */
@Data
public class DownloadInfo {

    /**
     * SHA1
     */
    private String sha1;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 下载地址
     */
    private String url;

}