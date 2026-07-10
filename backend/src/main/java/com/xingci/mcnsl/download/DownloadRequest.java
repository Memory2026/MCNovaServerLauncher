package com.xingci.mcnsl.download;

import lombok.Data;

import java.nio.file.Path;

/**
 * 下载请求
 *
 * 描述一个需要下载的文件。
 * 一个 DownloadRequest 对应一个文件。
 */
@Data
public class DownloadRequest {

    /**
     * 下载地址
     */
    private String url;

    /**
     * 保存位置
     */
    private Path target;

    /**
     * SHA1
     */
    private String sha1;

    /**
     * SHA256（部分 NeoForge 使用）
     */
    private String sha256;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 最大重试次数
     */
    private int retry = 3;

    /**
     * 是否覆盖已有文件
     */
    private boolean overwrite = false;

    public DownloadRequest() {
    }

    public DownloadRequest(String url, Path target) {
        this.url = url;
        this.target = target;
    }

    public DownloadRequest(String url, Path target, String sha1) {
        this.url = url;
        this.target = target;
        this.sha1 = sha1;
    }

    /**
     * 是否需要 SHA1 校验
     */
    public boolean hasSha1() {
        return sha1 != null && !sha1.isBlank();
    }

    /**
     * 是否需要 SHA256 校验
     */
    public boolean hasSha256() {
        return sha256 != null && !sha256.isBlank();
    }

    /**
     * 是否需要校验大小
     */
    public boolean hasSize() {
        return size > 0;
    }
}