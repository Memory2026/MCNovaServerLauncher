package com.xingci.mcnsl.minecraft.install;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.xingci.mcnsl.download.DownloadService;

import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;


/**
 * Minecraft资源安装器
 *
 * 安装:
 *
 * assets/indexes
 * assets/objects
 */
@Service
public class AssetInstaller {



    private final DownloadService downloadService;


    private final ObjectMapper mapper =
            new ObjectMapper();




    public AssetInstaller(
            DownloadService downloadService
    ){

        this.downloadService =
                downloadService;

    }









    /**
     * 安装assets
     *
     * @param versionJson 版本json
     * @param gameDir 游戏目录
     */
    public void install(
            JsonNode versionJson,
            Path gameDir
    )
            throws Exception {



        JsonNode assetIndex =
                versionJson
                        .path("assetIndex");





        if(assetIndex.isMissingNode()){


            throw new IOException(
                    "版本没有assetIndex"
            );


        }






        String id =

                assetIndex
                        .path("id")
                        .asText();




        String url =

                assetIndex
                        .path("url")
                        .asText();





        if(url.isBlank()){


            throw new IOException(
                    "assetIndex URL为空"
            );


        }






        /*
         * 下载index
         */
        Path indexFile =

                gameDir

                        .resolve("assets")

                        .resolve("indexes")

                        .resolve(
                                id
                                        +
                                        ".json"
                        );





        Files.createDirectories(
                indexFile.getParent()
        );






        if(!Files.exists(indexFile)){



            downloadService.download(

                    "Asset Index " + id,

                    url,

                    indexFile

            );


        }







        /*
         * 解析objects
         */
        JsonNode index =

                mapper.readTree(

                        indexFile.toFile()

                );





        JsonNode objects =

                index
                        .path("objects");






        if(!objects.isObject()){


            return;


        }







        Iterator<Map.Entry<String,JsonNode>> iterator =

                objects.fields();






        while(iterator.hasNext()){


            Map.Entry<String,JsonNode> entry =

                    iterator.next();




            JsonNode object =

                    entry.getValue();




            downloadObject(

                    object,

                    gameDir

            );


        }



    }









    /**
     * 下载单个资源
     */
    private void downloadObject(
            JsonNode object,
            Path gameDir
    ){



        String hash =

                object
                        .path("hash")
                        .asText();





        long size =

                object
                        .path("size")
                        .asLong();






        if(hash.isBlank()){


            return;


        }






        String prefix =

                hash.substring(
                        0,
                        2
                );






        Path target =

                gameDir

                        .resolve("assets")

                        .resolve("objects")

                        .resolve(prefix)

                        .resolve(hash);






        try{



            if(Files.exists(target)

                    &&

                    Files.size(target)==size){


                return;


            }





            String url =

                    "https://resources.download.minecraft.net/"

                            +

                            prefix

                            +

                            "/"

                            +

                            hash;






            downloadService.download(

                    "Asset " + hash,

                    url,

                    target

            );




        }
        catch(Exception ignored){


        }


    }



}