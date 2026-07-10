package com.xingci.mcnsl.api.minecraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 操作系统规则
 *
 * 例如：
 *
 * "os": {
 *   "name": "windows",
 *   "arch": "x86_64",
 *   "version": "^10\\."
 * }
 */
@Data
public class OsRule {

    /**
     * windows
     * linux
     * osx
     */
    private String name;

    /**
     * x86
     * x86_64
     * arm64
     */
    private String arch;

    /**
     * 系统版本正则
     */
    @JsonProperty("version")
    private String version;

}