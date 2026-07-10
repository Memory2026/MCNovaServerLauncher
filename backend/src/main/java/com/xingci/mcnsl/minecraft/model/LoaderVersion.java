package com.xingci.mcnsl.minecraft.model;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LoaderVersion {


    /**
     * Minecraft版本
     */
    private String minecraftVersion;



    /**
     * Loader类型
     */
    private String loader;



    /**
     * Loader版本
     */
    private String version;



    /**
     * 下载地址
     */
    private String url;


}