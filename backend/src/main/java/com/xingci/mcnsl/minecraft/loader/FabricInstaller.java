package com.xingci.mcnsl.minecraft.loader;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Fabric安装器
 *
 * 负责生成Fabric版本JSON
 */
@Service
public class FabricInstaller {


    private static final String FABRIC_META =
            "https://meta.fabricmc.net/v2/versions/loader/";



    private final ObjectMapper mapper =
            new ObjectMapper();



    private final HttpClient client =
            HttpClient.newHttpClient();







    /**
     * 安装Fabric
     *
     * @param minecraftVersion Minecraft版本
     * @param loaderVersion Fabric Loader版本
     * @param gameDir 游戏目录
     */
    public void install(
            String minecraftVersion,
            String loaderVersion,
            Path gameDir
    )
            throws Exception {



        String versionName =
                "fabric-loader-"
                        +
                        loaderVersion
                        +
                        "-"
                        +
                        minecraftVersion;




        Path versionDir =
                gameDir
                        .resolve("versions")
                        .resolve(versionName);




        Files.createDirectories(
                versionDir
        );




        /*
         * 获取Fabric JSON
         */
        JsonNode fabricJson =
                requestFabricJson(
                        minecraftVersion,
                        loaderVersion
                );




        /*
         * 生成启动JSON
         */
        ObjectNode result =
                createVersionJson(
                        fabricJson,
                        minecraftVersion,
                        versionName
                );




        Path jsonFile =
                versionDir
                        .resolve(
                                versionName
                                        +
                                        ".json"
                        );




        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(
                        jsonFile.toFile(),
                        result
                );


    }


    /**
     * 请求Fabric Meta
     */
    private JsonNode requestFabricJson(
            String mc,
            String loader
    )
            throws IOException, InterruptedException {



        String url =
                FABRIC_META
                        +
                        mc
                        +
                        "/"
                        +
                        loader
                        +
                        "/json";





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





        if(response.statusCode()!=200){


            throw new IOException(
                    "Fabric API错误:"
                            +
                            response.statusCode()
            );


        }





        return mapper.readTree(
                response.body()
        );

    }









    /**
     * 创建Minecraft版本JSON
     */
    private ObjectNode createVersionJson(
            JsonNode fabric,
            String minecraftVersion,
            String versionName
    ){



        ObjectNode json =
                mapper.createObjectNode();




        /*
         * 继承原版
         */
        json.put(
                "inheritsFrom",
                minecraftVersion
        );




        json.put(
                "id",
                versionName
        );





        json.put(
                "type",
                "release"
        );







        /*
         * mainClass
         */
        String mainClass =
                fabric.path("mainClass")
                        .asText();



        if(mainClass.isBlank()){


            mainClass =
                    "net.fabricmc.loader.impl.launch.knot.KnotClient";


        }




        json.put(
                "mainClass",
                mainClass
        );







        /*
         * Libraries
         */
        ArrayNode libraries =
                mapper.createArrayNode();



        JsonNode libs =
                fabric.path(
                        "libraries"
                );



        if(libs.isArray()){


            libs.forEach(
                    libraries::add
            );


        }



        json.set(
                "libraries",
                libraries
        );







        /*
         * arguments
         */
        if(fabric.has("arguments")){


            json.set(
                    "arguments",
                    fabric.get("arguments")
            );


        }



        return json;

    }


}