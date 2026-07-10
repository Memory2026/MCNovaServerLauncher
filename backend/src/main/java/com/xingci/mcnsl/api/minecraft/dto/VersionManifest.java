package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

import java.util.List;

/**
 * Mojang version_manifest_v2.json
 */
@Data
public class VersionManifest {

    /**
     * 最新版本
     */
    private Latest latest;

    /**
     * 全部版本
     */
    private List<VersionManifestVersion> versions;

    @Data
    public static class Latest {

        /**
         * 最新正式版
         */
        private String release;

        /**
         * 最新快照版
         */
        private String snapshot;

    }

}