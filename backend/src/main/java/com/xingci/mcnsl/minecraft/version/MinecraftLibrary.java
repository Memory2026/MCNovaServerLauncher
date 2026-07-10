package com.xingci.mcnsl.minecraft.version;


import lombok.Data;


/**
 * Minecraft依赖库
 */
@Data
public class MinecraftLibrary {


    /**
     * Maven坐标
     *
     * 例如:
     * org.lwjgl:lwjgl:3.3.3
     */
    private String name;



    /**
     * 下载地址
     */
    private String url;



    /**
     * 本地路径
     */
    private String path;



    /**
     * SHA1
     */
    private String sha1;



    /**
     * 文件大小
     */
    private long size;



    /**
     * Native库
     */
    private boolean nativeLibrary;


    /**
     * Native 解压目标路径
     */
    private String nativePath;


}
