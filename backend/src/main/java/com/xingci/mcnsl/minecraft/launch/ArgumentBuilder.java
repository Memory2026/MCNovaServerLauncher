package com.xingci.mcnsl.minecraft.launch;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.xingci.mcnsl.minecraft.util.UUIDUtils;
import com.xingci.mcnsl.minecraft.version.MinecraftVersion;



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
            String username,
            String uuid,
            String accessToken,
            String userType
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
         *
         * 如果未指定，使用离线UUID（与PCL/HMCL一致）
         */
        args.add("--uuid");

        if (uuid == null || uuid.isEmpty() || "00000000-0000-0000-0000-000000000000".equals(uuid)) {
            args.add(
                    UUIDUtils.generateOfflineUUIDString(username)
            );
        } else {
            args.add(uuid);
        }








        /*
         * Token
         */
        args.add("--accessToken");


        args.add(
                accessToken == null || accessToken.isEmpty()
                        ?
                        "0"
                        :
                        accessToken
        );








        /*
         * 用户类型
         */
        args.add("--userType");


        args.add(
                userType == null || userType.isEmpty()
                        ?
                        "legacy"
                        :
                        userType
        );








        return args;

    }


}