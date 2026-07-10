package com.xingci.mcnsl.api.minecraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * version_manifest_v2.json 中的版本信息
 */
@Data
public class VersionManifestVersion {

    /**
     * Minecraft版本
     * 例如：1.21.8
     */
    private String id;

    /**
     * release
     * snapshot
     * old_beta
     * old_alpha
     */
    private String type;

    /**
     * 指向版本JSON
     *
     * 例如：
     * https://piston-meta.mojang.com/v1/packages/xxxxx.json
     */
    private String url;

    /**
     * 更新时间
     */
    private String time;

    /**
     * 发布时间
     */
    @JsonProperty("releaseTime")
    private String releaseTime;

    /**
     * SHA1
     */
    private String sha1;

    /**
     * 是否符合规则
     */
    public boolean isRelease() {
        return "release".equals(type);
    }

    public boolean isSnapshot() {
        return "snapshot".equals(type);
    }

    public boolean isOldBeta() {
        return "old_beta".equals(type);
    }

    public boolean isOldAlpha() {
        return "old_alpha".equals(type);
    }

}