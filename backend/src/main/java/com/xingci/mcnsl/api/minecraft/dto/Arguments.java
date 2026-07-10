package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft 启动参数
 *
 * 1.13+：
 *
 * "arguments": {
 *   "game": [...],
 *   "jvm": [...]
 * }
 */
@Data
public class Arguments {

    /**
     * 游戏参数
     */
    private List<Object> game = new ArrayList<>();

    /**
     * JVM 参数
     */
    private List<Object> jvm = new ArrayList<>();

}