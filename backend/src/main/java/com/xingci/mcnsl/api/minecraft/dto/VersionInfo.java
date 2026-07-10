package com.xingci.mcnsl.api.minecraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft Version JSON
 *
 * 对应：
 * https://piston-meta.mojang.com/
 *
 * 一个 version.json 的完整信息。
 */
@Data
public class VersionInfo {

    /**
     * 版本号
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
     * 发布时间
     */
    private String time;

    /**
     * 发布时间（首次）
     */
    private String releaseTime;

    /**
     * Main Class
     */
    private String mainClass;

    /**
     * Asset Index
     */
    private AssetIndex assetIndex;

    /**
     * Assets ID
     */
    private String assets;

    /**
     * 下载信息
     */
    private Downloads downloads;

    /**
     * Java Version
     */
    private JavaVersion javaVersion;

    /**
     * Libraries
     */
    private List<Library> libraries = new ArrayList<>();

    /**
     * Arguments（1.13+）
     */
    private Arguments arguments;

    /**
     * Minecraft Arguments（1.12-）
     */
    private String minecraftArguments;

    /**
     * Logging
     */
    private Logging logging;

    /**
     * 继承版本
     *
     * Forge/Fabric/NeoForge 会使用
     */
    private String inheritsFrom;

    /**
     * Launcher 最低版本
     */
    private int minimumLauncherVersion;

    /**
     * Compliance Level
     */
    @JsonProperty("complianceLevel")
    private int complianceLevel;

    /**
     * 是否继承其它版本
     */
    public boolean isInherited() {
        return inheritsFrom != null && !inheritsFrom.isBlank();
    }

    /**
     * 是否新版 Arguments
     */
    public boolean hasArguments() {
        return arguments != null;
    }

    /**
     * 是否旧版 Arguments
     */
    public boolean hasMinecraftArguments() {
        return minecraftArguments != null
                && !minecraftArguments.isBlank();
    }

    /**
     * 是否包含 Libraries
     */
    public boolean hasLibraries() {
        return libraries != null
                && !libraries.isEmpty();
    }

    /**
     * 是否需要 Java Version
     */
    public boolean hasJavaVersion() {
        return javaVersion != null;
    }

    /**
     * 是否有下载信息
     */
    public boolean hasDownloads() {
        return downloads != null;
    }

    /**
     * 是否有 Logging
     */
    public boolean hasLogging() {
        return logging != null;
    }

    /**
     * 是否有 AssetIndex
     */
    public boolean hasAssetIndex() {
        return assetIndex != null;
    }

}