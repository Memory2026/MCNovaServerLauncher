package com.xingci.mcnsl.minecraft.loader;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LoaderVersion {


    /**
     * 加载器名称
     */
    private String loader;


    /**
     * Minecraft版本
     */
    private String minecraftVersion;


    /**
     * Loader版本
     */
    private String version;


    /**
     * 下载地址
     */
    private String url;



}