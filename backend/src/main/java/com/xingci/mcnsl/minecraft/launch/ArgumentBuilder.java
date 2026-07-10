package com.xingci.mcnsl.minecraft.launch;


import com.xingci.mcnsl.minecraft.version.MinecraftVersion;

import org.springframework.stereotype.Component;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Minecraft启动参数生成器
 *
 * 负责生成:
 *
 * --username
 * --version
 * --gameDir
 * --assetsDir
 * --assetIndex
 * --uuid
 * --accessToken
 *
 */
@Component
public class ArgumentBuilder {



    /**
     * 生成游戏参数
     */
    public List<String> build(
            MinecraftVersion version,
            Path gameDir,
            String username
    ){


        List<String> args =

                new ArrayList<>();





        /*
         * 玩家名称
         */
        args.add("--username");

        args.add(
                username == null
                        ?
                        "Player"
                        :
                        username
        );







        /*
         * 版本
         */
        args.add("--version");

        args.add(
                version.getId()
        );








        /*
         * 游戏目录
         */
        args.add("--gameDir");

        args.add(
                gameDir.toAbsolutePath()
                        .toString()
        );








        /*
         * Assets目录
         */
        args.add("--assetsDir");

        args.add(

                gameDir

                        .resolve(
                                "assets"
                        )

                        .toAbsolutePath()
                        .toString()

        );








        /*
         * Asset Index
         */
        if(version.getAssetIndexId()!=null){


            args.add(
                    "--assetIndex"
            );


            args.add(
                    version.getAssetIndexId()
            );


        }








        /*
         * UUID
         */
        args.add("--uuid");


        args.add(
                UUID.randomUUID()
                        .toString()
        );








        /*
         * Token
         *
         * 离线模式暂时填0
         */
        args.add("--accessToken");


        args.add(
                "0"
        );








        /*
         * 用户类型
         */
        args.add("--userType");


        args.add(
                "legacy"
        );





        return args;

    }


}