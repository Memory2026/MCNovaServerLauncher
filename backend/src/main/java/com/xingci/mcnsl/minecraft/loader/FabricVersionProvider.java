package com.xingci.mcnsl.minecraft.loader;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingci.mcnsl.minecraft.model.LoaderVersion;

import org.springframework.stereotype.Component;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;



@Component("minecraftFabricVersionProvider")
public class FabricVersionProvider {


    private static final String API =
            "https://meta.fabricmc.net/v2/versions/loader";



    private final ObjectMapper mapper =
            new ObjectMapper();



    private final HttpClient client =
            HttpClient.newHttpClient();




    /**
     * 获取 Fabric Loader
     *
     * @param minecraftVersion Minecraft版本
     */
    public List<LoaderVersion> getVersions(
            String minecraftVersion
    ){


        List<LoaderVersion> result =
                new ArrayList<>();


        try {


            String url =
                    API
                            +
                            "/"
                            +
                            minecraftVersion;



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



            JsonNode root =
                    mapper.readTree(
                            response.body()
                    );



            for(JsonNode node:root){


                JsonNode loader =
                        node.path("loader");



                String version =
                        loader.path("version")
                                .asText();



                String download =
                        "https://meta.fabricmc.net/v2/versions/loader/"
                                +
                                minecraftVersion
                                +
                                "/"
                                +
                                version;



                result.add(
                        new LoaderVersion(
                                minecraftVersion,
                                "Fabric",
                                version,
                                download
                        )
                );


            }



        }catch(Exception e){


            e.printStackTrace();


        }



        return result;

    }



}
