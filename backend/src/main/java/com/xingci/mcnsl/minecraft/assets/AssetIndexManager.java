package com.xingci.mcnsl.minecraft.assets;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.xingci.mcnsl.minecraft.version.MinecraftVersion;

import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



/**
 * Minecraft Asset索引管理器
 *
 * 负责:
 *
 * 1. 保存Asset Index
 * 2. 定位objects目录
 * 3. 提供资源路径
 */
@Component
public class AssetIndexManager {



    private final ObjectMapper mapper =
            new ObjectMapper();









    /**
     * 获取Asset Index文件
     */
    public Path getIndexFile(
            MinecraftVersion version,
            Path gameDir
    ){


        return gameDir

                .resolve(
                        "assets"
                )

                .resolve(
                        "indexes"
                )

                .resolve(
                        version.getAssetIndexId()
                                +
                                ".json"
                );

    }









    /**
     * 读取Asset Index
     */
    public JsonNode readIndex(
            MinecraftVersion version,
            Path gameDir
    )
            throws IOException {



        Path file =
                getIndexFile(
                        version,
                        gameDir
                );



        if(!Files.exists(file)){


            throw new IOException(
                    "Asset Index不存在:"
                            +
                            file
            );


        }



        return mapper.readTree(

                Files.readString(
                        file
                )

        );

    }









    /**
     * 根据hash获取资源文件
     *
     * hash:
     *
     * abcdef123456
     *
     * 返回:
     *
     * assets/objects/ab/abcdef123456
     */
    public Path resolveObject(
            String hash,
            Path gameDir
    ){


        if(hash==null
                ||
                hash.length()<2){


            return null;

        }




        String prefix =
                hash.substring(
                        0,
                        2
                );



        return gameDir

                .resolve(
                        "assets"
                )

                .resolve(
                        "objects"
                )

                .resolve(
                        prefix
                )

                .resolve(
                        hash
                );


    }









    /**
     * 判断资源是否存在
     */
    public boolean exists(
            String hash,
            Path gameDir
    ){


        Path path =
                resolveObject(
                        hash,
                        gameDir
                );


        return path != null
                &&
                Files.exists(path);

    }


}