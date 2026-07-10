package com.xingci.mcnsl.minecraft.launch;


import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



/**
 * Minecraft Native提取器
 *
 * 解压:
 *
 * lwjgl.dll
 * liblwjgl.so
 * liblwjgl.dylib
 *
 */
@Component
public class NativeExtractor {



    /**
     * 提取natives
     *
     * @param versionJson 版本json
     * @param gameDir 游戏目录
     */
    public void extract(
            JsonNode versionJson,
            Path gameDir
    )
            throws Exception {



        Path nativesDir =

                gameDir

                        .resolve(
                                "natives"
                        );



        Files.createDirectories(
                nativesDir
        );






        JsonNode libraries =

                versionJson

                        .path("libraries");





        if(!libraries.isArray()){


            return;


        }







        Set<Path> extracted =
                new HashSet<>();







        for(JsonNode library:libraries){



            JsonNode downloads =

                    library
                            .path("downloads");




            JsonNode classifiers =

                    downloads
                            .path("classifiers");





            if(!classifiers.isObject()){


                continue;


            }







            String nativeKey =
                    getNativeKey();





            JsonNode nativeJar =

                    classifiers
                            .path(nativeKey);





            if(nativeJar.isMissingNode()){


                continue;


            }






            String path =

                    nativeJar
                            .path("path")
                            .asText();





            if(path.isBlank()){


                continue;


            }





            Path jar =

                    gameDir

                            .resolve(
                                    "libraries"
                            )

                            .resolve(
                                    path
                            );





            if(!Files.exists(jar)){


                continue;


            }





            if(extracted.contains(jar)){


                continue;


            }






            unzipNative(
                    jar,
                    nativesDir
            );




            extracted.add(jar);



        }



    }









    /**
     * 解压native jar
     */
    private void unzipNative(
            Path jar,
            Path target
    )
            throws Exception {



        try(

                InputStream input =

                        Files.newInputStream(
                                jar
                        );


                ZipInputStream zip =

                        new ZipInputStream(
                                input
                        )

        ){



            ZipEntry entry;





            while(
                    (entry=zip.getNextEntry())
                            != null
            ){



                if(entry.isDirectory()){


                    continue;


                }







                String name =
                        entry.getName();





                /*
                 * 排除无关文件
                 */
                if(!isNativeFile(name)){


                    continue;


                }





                Path output =

                        target

                                .resolve(
                                        Path.of(name)
                                                .getFileName()
                                );





                Files.copy(

                        zip,

                        output,

                        StandardCopyOption.REPLACE_EXISTING

                );



            }



        }


    }









    /**
     * native文件过滤
     */
    private boolean isNativeFile(
            String name
    ){


        return name.endsWith(".dll")

                ||

                name.endsWith(".so")

                ||

                name.endsWith(".dylib")

                ||

                name.endsWith(".jnilib");


    }









    /**
     * 获取当前系统native名称
     */
    private String getNativeKey(){



        String os =

                System.getProperty(
                                "os.name"
                        )
                        .toLowerCase();





        if(os.contains("win")){


            return "natives-windows";


        }



        if(os.contains("mac")){


            return "natives-osx";


        }



        return "natives-linux";


    }


}