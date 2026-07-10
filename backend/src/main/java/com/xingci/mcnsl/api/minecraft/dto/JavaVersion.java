package com.xingci.mcnsl.api.minecraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Minecraft 所需Java版本
 *
 * 例如：
 *
 * "javaVersion": {
 *   "component": "java-runtime-gamma",
 *   "majorVersion": 21
 * }
 */
@Data
public class JavaVersion {

    /**
     * Java组件
     */
    private String component;

    /**
     * Java主版本
     */
    @JsonProperty("majorVersion")
    private int majorVersion;

}