package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

/**
 * 资源索引
 *
 * version.json：
 *
 * "assetIndex": {
 *   "id": "24",
 *   "sha1": "...",
 *   "size": 87563,
 *   "totalSize": 637485214,
 *   "url": "https://..."
 * }
 */
@Data
public class AssetIndex {

    /**
     * Asset版本
     */
    private String id;

    /**
     * SHA1
     */
    private String sha1;

    /**
     * 索引文件大小
     */
    private long size;

    /**
     * 全部资源大小
     */
    private long totalSize;

    /**
     * 下载地址
     */
    private String url;

}