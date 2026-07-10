package com.xingci.mcnsl.minecraft.launch;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.nio.file.Path;



/**
 * Minecraft启动配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaunchOptions {



    /**
     * Java路径
     */
    private Path javaPath;





    /**
     * 游戏目录
     */
    private Path gameDir;





    /**
     * Minecraft版本
     */
    private String version;





    /**
     * 玩家名称
     */
    @Builder.Default
    private String username =
            "Player";





    /**
     * 玩家UUID
     */
    @Builder.Default
    private String uuid =

            "00000000-0000-0000-0000-000000000000";





    /**
     * 登录Token
     */
    @Builder.Default
    private String accessToken =

            "0";





    /**
     * 用户类型
     */
    @Builder.Default
    private String userType =

            "legacy";





    /**
     * Microsoft ClientId
     */
    private String clientId;





    /**
     * Demo账号
     */
    private boolean demo;





    /**
     * Assets目录
     */
    private Path assetsDir;





    /**
     * Asset索引
     */
    private String assetIndex;





    /**
     * 最小内存 MB
     */
    @Builder.Default
    private int minMemory =

            1024;





    /**
     * 最大内存 MB
     */
    @Builder.Default
    private int maxMemory =

            4096;



}