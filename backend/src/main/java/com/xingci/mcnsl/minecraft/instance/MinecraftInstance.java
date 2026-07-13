package com.xingci.mcnsl.minecraft.instance;


import lombok.Data;


import java.time.LocalDateTime;
import java.util.UUID;



/**
 * Minecraft客户端实例
 *
 * 一个实例对应一个游戏目录
 */
@Data
public class MinecraftInstance {



    /**
     * 实例ID
     */
    private String id =
            UUID.randomUUID().toString();





    /**
     * 实例名称
     */
    private String name;





    /**
     * Minecraft版本
     */
    private String version;





    /**
     * 加载器
     *
     * vanilla
     * fabric
     * forge
     * neoforge
     */
    private String loader = "vanilla";





    /**
     * 游戏目录
     */
    private String path;





    /**
     * Java路径
     */
    private String javaPath;





    /**
     * 最小内存 MB
     */
    private int minMemory = 1024;





    /**
     * 最大内存 MB
     */
    private int maxMemory = 4096;

    /**
     * JVM参数
     */
    private String jvmParameters = "-Xms1G -Xmx4G";





    /**
     * 玩家名称
     */
    private String username =
            "Player";





    /**
     * 状态
     *
     * CREATED
     * INSTALLED
     * RUNNING
     */
    private String status =
            "CREATED";





    /**
     * 创建时间
     */
    private LocalDateTime createTime =
            LocalDateTime.now();





    /**
     * 最近启动时间
     */
    private LocalDateTime lastLaunchTime;



}