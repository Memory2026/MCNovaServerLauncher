package com.xingci.mcnsl.minecraft.version;


import org.springframework.stereotype.Component;


import java.nio.file.Path;



/**
 * Minecraft版本加载器
 *
 * 负责:
 *
 * 1. 定位版本文件
 * 2. 调用VersionParser解析
 *
 */
@Component
public class MinecraftVersionLoader {



    private final VersionParser versionParser;




    public MinecraftVersionLoader(
            VersionParser versionParser
    ){

        this.versionParser =
                versionParser;

    }








    /**
     * 加载Minecraft版本
     *
     * @param gameDir 游戏目录
     * @param version 版本号
     */
    public MinecraftVersion load(
            Path gameDir,
            String version
    )
            throws Exception {



        Path versionJson =

                gameDir

                        .resolve(
                                "versions"
                        )

                        .resolve(
                                version
                        )

                        .resolve(
                                version + ".json"
                        );





        return versionParser.parse(

                versionJson,

                gameDir

        );


    }



}