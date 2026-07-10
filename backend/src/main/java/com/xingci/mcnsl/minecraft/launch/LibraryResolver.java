package com.xingci.mcnsl.minecraft.launch;


import com.xingci.mcnsl.minecraft.version.MinecraftLibrary;
import com.xingci.mcnsl.minecraft.version.MinecraftVersion;

import org.springframework.stereotype.Component;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



/**
 * Minecraft Library解析器
 *
 * 根据MinecraftVersion生成classpath
 */
@Component
public class LibraryResolver {



    /**
     * 解析Libraries
     *
     * @param version Minecraft版本
     * @param gameDir 游戏目录
     */
    public List<Path> resolve(
            MinecraftVersion version,
            Path gameDir
    ){


        List<Path> result =
                new ArrayList<>();



        for(MinecraftLibrary library :
                version.getLibraries()){



            Path path =
                    resolveLibrary(
                            library,
                            gameDir
                    );



            if(path != null){

                result.add(path);

            }


        }



        return result;

    }









    /**
     * 解析单个Library
     */
    private Path resolveLibrary(
            MinecraftLibrary library,
            Path gameDir
    ){


        if(library.getPath()!=null
                &&
                !library.getPath().isBlank()){


            return gameDir
                    .resolve(
                            "libraries"
                    )
                    .resolve(
                            library.getPath()
                    );

        }




        if(library.getName()==null
                ||
                library.getName().isBlank()){


            return null;

        }




        return resolveMavenPath(
                library.getName(),
                gameDir
        );

    }









    /**
     * Maven坐标转换
     *
     * group:artifact:version
     */
    private Path resolveMavenPath(
            String name,
            Path gameDir
    ){


        String[] parts =
                name.split(":");



        if(parts.length < 3){

            return null;

        }



        String group =
                parts[0]
                        .replace(
                                ".",
                                "/"
                        );


        String artifact =
                parts[1];


        String version =
                parts[2];



        String fileName =
                artifact
                        +
                        "-"
                        +
                        version
                        +
                        ".jar";



        return gameDir

                .resolve(
                        "libraries"
                )

                .resolve(
                        group
                )

                .resolve(
                        artifact
                )

                .resolve(
                        version
                )

                .resolve(
                        fileName
                );


    }



}