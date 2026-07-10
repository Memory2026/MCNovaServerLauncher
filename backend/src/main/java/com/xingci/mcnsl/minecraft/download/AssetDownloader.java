package com.xingci.mcnsl.minecraft.download;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.xingci.mcnsl.minecraft.version.MinecraftVersion;


import org.springframework.stereotype.Component;


import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;



/**
 * Minecraft Assets 下载器
 *
 * 下载:
 *
 * assets/indexes
 * assets/objects
 */
@Component
public class AssetDownloader {



    private final HttpClient client =
            HttpClient.newHttpClient();



    private final ObjectMapper mapper =
            new ObjectMapper();








    /**
     * 下载Assets
     */
    public void downloadAssets(
            MinecraftVersion version,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {



        String url =
                version.getAssetIndexUrl();





        if(url==null
                ||
                url.isBlank()){


            return;


        }






        task.setStep(
                "下载Asset Index"
        );







        JsonNode index =

                downloadIndex(
                        url
                );









        /*
         * 保存index
         */
        Path indexDir =

                gameDir

                        .resolve("assets")

                        .resolve("indexes");



        Files.createDirectories(
                indexDir
        );





        Path indexFile =

                indexDir

                        .resolve(

                                version.getAssetIndexId()
                                        +
                                        ".json"

                        );





        Files.writeString(

                indexFile,

                index.toPrettyString()

        );









        downloadObjects(

                index,

                gameDir,

                task

        );


    }









    /**
     * 下载index
     */
    private JsonNode downloadIndex(
            String url
    )
            throws Exception {



        HttpRequest request =

                HttpRequest.newBuilder()

                        .uri(
                                URI.create(url)
                        )

                        .GET()

                        .build();






        HttpResponse<String> response =

                client.send(

                        request,

                        HttpResponse.BodyHandlers.ofString()

                );





        return mapper.readTree(

                response.body()

        );


    }









    /**
     * 下载objects
     */
    private void downloadObjects(
            JsonNode index,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {



        JsonNode objects =

                index.path(
                        "objects"
                );





        if(!objects.isObject()){


            return;


        }






        int total =
                objects.size();



        int current = 0;







        Iterator<Map.Entry<String, JsonNode>> iterator =

                objects.fields();







        while(iterator.hasNext()){



            Map.Entry<String, JsonNode> entry =

                    iterator.next();




            current++;




            JsonNode object =
                    entry.getValue();





            String hash =

                    object.path(
                                    "hash"
                            )

                            .asText();






            if(hash.isBlank()){

                continue;

            }







            String prefix =

                    hash.substring(
                            0,
                            2
                    );







            String url =

                    "https://resources.download.minecraft.net/"
                            +
                            prefix
                            +
                            "/"
                            +
                            hash;








            Path target =

                    gameDir

                            .resolve("assets")

                            .resolve("objects")

                            .resolve(prefix)

                            .resolve(hash);








            if(Files.exists(target)){


                continue;


            }








            Files.createDirectories(

                    target.getParent()

            );







            task.setCurrentFile(

                    hash

            );



            task.setStep(

                    "下载Assets"

            );







            downloadFile(

                    url,

                    target

            );







            task.update(

                    current,

                    total

            );



        }



    }









    /**
     * 下载文件
     */
    private void downloadFile(
            String url,
            Path target
    )
            throws IOException, InterruptedException {



        HttpRequest request =

                HttpRequest.newBuilder()

                        .uri(
                                URI.create(url)
                        )

                        .GET()

                        .build();







        HttpResponse<byte[]> response =

                client.send(

                        request,

                        HttpResponse.BodyHandlers.ofByteArray()

                );







        Files.write(

                target,

                response.body()

        );


    }


}