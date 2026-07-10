package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

/**
 * Minecraft Rule
 *
 * 例如：
 *
 * {
 *   "action": "allow",
 *   "os": {
 *      "name": "windows"
 *   }
 * }
 */
@Data
public class Rule {

    /**
     * allow
     * disallow
     */
    private String action;

    /**
     * 操作系统规则
     */
    private OsRule os;

    /**
     * 是否允许
     */
    public boolean isAllow() {
        return "allow".equals(action);
    }

    /**
     * 是否禁止
     */
    public boolean isDisallow() {
        return "disallow".equals(action);
    }

}