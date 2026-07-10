package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

/**
 * Library Artifact
 *
 * {
 *   "path": "...",
 *   "sha1": "...",
 *   "size": 12345,
 *   "url": "https://..."
 * }
 */
@Data
public class Artifact {

    /**
     * 在 libraries 下的相对路径
     *
     * 例如：
     * com/google/guava/guava/33.2.1-jre/guava-33.2.1-jre.jar
     */
    private String path;

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