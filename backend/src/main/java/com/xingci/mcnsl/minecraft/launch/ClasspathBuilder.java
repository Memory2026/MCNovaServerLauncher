package com.xingci.mcnsl.minecraft.launch;


import com.xingci.mcnsl.minecraft.version.MinecraftLibrary;
import com.xingci.mcnsl.minecraft.version.MinecraftVersion;


import org.springframework.stereotype.Component;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



/**
 * Minecraft Classpath生成器
 *
 * 负责生成:
 *
 * libraries/*.jar
 * client.jar
 *
 */
@Component
public class ClasspathBuilder {



    /**
     * 生成classpath
     */
    public List<Path> build(
            MinecraftVersion version,
            Path gameDir
    ){


        List<Path> classpath =

                new ArrayList<>();





        /*
         * Libraries
         */
        if(version.getLibraries()!=null){



            for(MinecraftLibrary library :
                    version.getLibraries()){

                if(library.isNativeLibrary()){

                    continue;

                }

                Path path =

                        resolveLibrary(
                                library,
                                gameDir
                        );



                if(path!=null){

                    classpath.add(path);

                }


            }


        }









        /*
         * client.jar
         */
        Path clientJar =

                gameDir

                        .resolve("versions")

                        .resolve(
                                version.getClientJarVersion() == null
                                        ||
                                        version.getClientJarVersion().isBlank()
                                        ?
                                        version.getId()
                                        :
                                        version.getClientJarVersion()
                        )

                        .resolve(
                                (
                                        version.getClientJarVersion() == null
                                                ||
                                                version.getClientJarVersion().isBlank()
                                                ?
                                                version.getId()
                                                :
                                                version.getClientJarVersion()
                                )
                                        +
                                        ".jar"
                        );





        classpath.add(
                clientJar
        );





        return classpath;


    }









    /**
     * 解析library路径
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







        /*
         * Maven坐标
         *
         * group:artifact:version
         */
        String name =
                library.getName();



        if(name==null){

            return null;

        }






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






        return gameDir

                .resolve(
                        "libraries"
                )

                .resolve(group)

                .resolve(artifact)

                .resolve(version)

                .resolve(
                        artifact
                                +
                                "-"
                                +
                                version
                                +
                                ".jar"
                );


    }


}
