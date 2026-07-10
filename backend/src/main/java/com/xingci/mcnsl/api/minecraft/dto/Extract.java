package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

import java.util.List;

/**
 * Native 解压规则
 *
 * 例如：
 *
 * "extract": {
 *   "exclude": [
 *     "META-INF/"
 *   ]
 * }
 */
@Data
public class Extract {

    /**
     * 解压时排除的目录
     */
    private List<String> exclude;

}