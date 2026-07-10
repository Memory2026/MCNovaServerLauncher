package com.xingci.mcnsl.minecraft.version;


import lombok.Data;

import java.util.List;



/**
 * Minecraft版本信息
 *
 * 对应 version.json
 */
@Data
public class MinecraftVersion {



    /**
     * 版本ID
     */
    private String id;



    /**
     * 类型
     *
     * release / snapshot
     */
    private String type;



    /**
     * 发布时间
     */
    private String releaseTime;



    /**
     * 主类
     */
    private String mainClass;





    /**
     * Assets索引ID
     */
    private String assetIndexId;



    /**
     * Assets索引URL
     */
    private String assetIndexUrl;



    /**
     * Assets SHA1
     */
    private String assetIndexSha1;



    /**
     * 客户端jar下载地址
     */
    private String clientUrl;



    /**
     * 客户端jar SHA1
     */
    private String clientSha1;


    /**
     * 实际客户端 Jar 所属版本
     */
    private String clientJarVersion;



    /**
     * 客户端大小
     */
    private long clientSize;





    /**
     * Java版本要求
     */
    private int javaVersion;





    /**
     * JVM参数
     */
    private List<String> jvmArguments;



    /**
     * 游戏参数
     */
    private List<String> gameArguments;





    /**
     * Libraries
     */
    private List<MinecraftLibrary> libraries;



}
