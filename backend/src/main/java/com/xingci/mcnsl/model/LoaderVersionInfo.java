package com.xingci.mcnsl.model;


import lombok.Data;


@Data
public class LoaderVersionInfo {


    /**
     * 加载器名称
     */
    private String loader;


    /**
     * Minecraft版本
     */
    private String minecraftVersion;


    /**
     * 加载器版本
     */
    private String version;


    /**
     * 是否推荐版本
     */
    private boolean recommended;


    /**
     * 推荐的Fabric API版本（仅Fabric加载器使用）
     */
    private String fabricApiVersion;


    /**
     * 版本类型：stable（稳定版）、beta（测试版）
     */
    private String versionType;


    /**
     * 发布时间
     */
    private String releaseTime;


    public LoaderVersionInfo(
            String loader,
            String version,
            String minecraftVersion
    ){
        this.loader = loader;
        this.version = version;
        this.minecraftVersion = minecraftVersion;
    }

}
