package com.xingci.mcnsl.api.minecraft.dto;

import lombok.Data;

import java.util.Map;

/**
 * Library 下载信息
 */
@Data
public class DownloadsLibrary {

    /**
     * 主 Jar
     */
    private Artifact artifact;

    /**
     * Native
     *
     * key：
     * linux
     * windows
     * osx
     */
    private Map<String, Artifact> classifiers;

}