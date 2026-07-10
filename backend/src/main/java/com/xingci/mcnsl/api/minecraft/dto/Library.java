package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Minecraft Library
 *
 * 示例：
 *
 * {
 *   "downloads": {
 *     "artifact": { ... },
 *     "classifiers": { ... }
 *   },
 *   "name": "com.google.guava:guava:33.2.1-jre",
 *   "rules": [ ... ],
 *   "natives": {
 *     "windows": "natives-windows",
 *     "linux": "natives-linux",
 *     "osx": "natives-macos"
 *   }
 * }
 */
@Data
public class Library {

    /**
     * Maven 坐标
     *
     * 例如：
     * com.google.guava:guava:33.2.1-jre
     */
    private String name;

    /**
     * 下载信息
     */
    private DownloadsLibrary downloads;

    /**
     * Native 映射
     *
     * key:
     * windows
     * linux
     * osx
     *
     * value:
     * natives-windows
     * natives-linux
     * natives-macos
     */
    private Map<String, String> natives;

    /**
     * 下载规则
     */
    private List<Rule> rules;

    /**
     * 旧版 Forge 使用的下载 URL
     *
     * 默认：
     * https://libraries.minecraft.net/
     */
    private String url;

    /**
     * Extract 配置
     *
     * Native 解压时需要排除 META-INF
     */
    private Extract extract;

    /**
     * SHA1（旧版本可能存在）
     */
    private List<String> checksums;

    /**
     * 是否存在下载信息
     */
    public boolean hasDownloads() {
        return downloads != null;
    }

    /**
     * 是否包含 Native
     */
    public boolean hasNatives() {
        return natives != null && !natives.isEmpty();
    }

    /**
     * 是否包含 Rules
     */
    public boolean hasRules() {
        return rules != null && !rules.isEmpty();
    }

    /**
     * 是否需要解压
     */
    public boolean needExtract() {
        return extract != null;
    }

}