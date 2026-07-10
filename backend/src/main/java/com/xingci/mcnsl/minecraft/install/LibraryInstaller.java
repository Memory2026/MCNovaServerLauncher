package com.xingci.mcnsl.minecraft.install;


import com.fasterxml.jackson.databind.JsonNode;

import com.xingci.mcnsl.download.DownloadService;

import org.springframework.stereotype.Service;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;


/**
 * Minecraft Library安装器
 *
 * 安装:
 *
 * .minecraft/libraries
 */
@Service
public class LibraryInstaller {



    private final DownloadService downloadService;





    public LibraryInstaller(
            DownloadService downloadService
    ){

        this.downloadService =
                downloadService;

    }









    /**
     * 安装libraries
     */
    public void install(
            JsonNode versionJson,
            Path gameDir
    ){


        JsonNode libraries =

                versionJson
                        .path("libraries");





        if(!libraries.isArray()){


            return;


        }







        for(JsonNode lib : libraries){



            try{


                if(!allowed(lib)){


                    continue;


                }





                downloadLibrary(

                        lib,

                        gameDir

                );



            }
            catch(Exception ignored){


            }


        }



    }









    /**
     * 下载单个Library
     */
    private void downloadLibrary(
            JsonNode lib,
            Path gameDir
    )
            throws Exception {



        JsonNode downloads =

                lib
                        .path("downloads");





        JsonNode artifact =

                downloads
                        .path("artifact");





        if(artifact.isMissingNode()){


            return;


        }







        String url =

                artifact
                        .path("url")
                        .asText();






        String path =

                artifact
                        .path("path")
                        .asText();





        if(url.isBlank()
                ||
                path.isBlank()){


            return;


        }







        Path target =

                gameDir

                        .resolve(
                                "libraries"
                        )

                        .resolve(
                                path
                        );






        if(Files.exists(target)){


            return;


        }







        Files.createDirectories(
                target.getParent()
        );






        downloadService.download(

                "Library "

                        +

                        target.getFileName(),

                url,

                target

        );



    }









    /**
     * 判断rules
     */
    private boolean allowed(
            JsonNode lib
    ){



        JsonNode rules =

                lib
                        .path("rules");





        if(!rules.isArray()){


            return true;


        }






        boolean allow = false;





        for(JsonNode rule : rules){



            String action =

                    rule
                            .path("action")
                            .asText();





            if("allow".equals(action)){


                allow = true;


            }




            JsonNode os =

                    rule
                            .path("os");





            if(os.isObject()){



                String name =

                        os
                                .path("name")
                                .asText();




                if(!matchOS(name)){


                    allow=false;


                }



            }



        }





        return allow;


    }









    /**
     * 系统判断
     */
    private boolean matchOS(
            String os
    ){



        String current =

                System.getProperty(
                                "os.name"
                        )
                        .toLowerCase();






        return switch(os){


            case "windows" ->
                    current.contains("win");



            case "linux" ->
                    current.contains("linux");



            case "osx" ->
                    current.contains("mac");



            default ->
                    true;


        };


    }



}