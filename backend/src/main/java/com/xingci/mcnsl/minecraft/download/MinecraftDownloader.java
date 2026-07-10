package com.xingci.mcnsl.minecraft.download;


import com.xingci.mcnsl.minecraft.version.MinecraftVersion;
import com.xingci.mcnsl.minecraft.version.VersionParser;


import org.springframework.stereotype.Component;


import java.nio.file.Path;



/**
 * Minecraft总下载器
 *
 * 负责完整客户端安装
 */
@Component
public class MinecraftDownloader {



    private final VersionParser versionParser;

    private final VersionManifestDownloader manifestDownloader;


    private final ClientDownloader clientDownloader;


    private final LibraryDownloader libraryDownloader;


    private final AssetDownloader assetDownloader;








    public MinecraftDownloader(
            VersionParser versionParser,
            VersionManifestDownloader manifestDownloader,
            ClientDownloader clientDownloader,
            LibraryDownloader libraryDownloader,
            AssetDownloader assetDownloader
    ){


        this.versionParser =
                versionParser;

        this.manifestDownloader =
                manifestDownloader;


        this.clientDownloader =
                clientDownloader;


        this.libraryDownloader =
                libraryDownloader;


        this.assetDownloader =
                assetDownloader;


    }









    /**
     * 下载Minecraft
     *
     * @param version Minecraft版本
     * @param gameDir 游戏目录
     * @param task 安装任务
     */
    public void download(
            String version,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {



        try {



            task.start();



            task.setStep(
                    "解析版本"
            );








            Path versionJson =
                    manifestDownloader.ensureVersionJson(
                            version,
                            gameDir,
                            task
                    );









            MinecraftVersion minecraftVersion =

                    versionParser.parse(

                            versionJson,

                            gameDir

                    );








            /*
             * 下载客户端
             */
            clientDownloader.downloadClient(

                    minecraftVersion,

                    gameDir,

                    task

            );









            /*
             * 下载Libraries
             */
            libraryDownloader.downloadLibraries(

                    minecraftVersion,

                    gameDir,

                    task

            );









            /*
             * 下载Assets
             */
            assetDownloader.downloadAssets(

                    minecraftVersion,

                    gameDir,

                    task

            );








        }
        catch(Exception e){



            task.failed(

                    e.getMessage()

            );


            throw e;


        }



    }



}
