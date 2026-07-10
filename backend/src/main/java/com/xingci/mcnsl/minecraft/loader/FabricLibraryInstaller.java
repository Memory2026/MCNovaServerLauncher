package com.xingci.mcnsl.minecraft.loader;


import com.fasterxml.jackson.databind.JsonNode;
import com.xingci.mcnsl.minecraft.download.InstallStatus;
import com.xingci.mcnsl.minecraft.download.MinecraftInstallTask;

import org.springframework.stereotype.Component;


import java.nio.file.Files;
import java.nio.file.Path;



/**
 * Fabric Library 安装器
 *
 * 负责安装 Fabric 所需 Libraries
 */
@Component
public class FabricLibraryInstaller {



    /**
     * 安装 Fabric Libraries
     *
     * @param libraries Fabric库列表
     * @param gameDir 游戏目录
     */
    public void install(
            JsonNode libraries,
            Path gameDir
    )
            throws Exception {



        Files.createDirectories(
                gameDir.resolve(
                        "libraries"
                )
        );



        for(JsonNode library : libraries){


            String path =
                    library.path("path")
                            .asText();



            if(path.isEmpty()){

                continue;

            }



            Path target =
                    gameDir.resolve(
                                    "libraries"
                            )
                            .resolve(
                                    path
                            );



            Files.createDirectories(
                    target.getParent()
            );


        }


    }



}