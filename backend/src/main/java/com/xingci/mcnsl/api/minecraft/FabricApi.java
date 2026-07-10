package com.xingci.mcnsl.api.minecraft;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;



@Component
public class FabricApi {


    private final HttpClient client =
            HttpClient.newHttpClient();


    private final ObjectMapper mapper =
            new ObjectMapper();



    public List<String> getVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {



        String url =
                "https://meta.fabricmc.net/v2/versions/loader/"
                        + minecraftVersion;



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



        List<String> result =
                new ArrayList<>();



        for(JsonNode node : root){


            result.add(
                    node.path("loader")
                            .path("version")
                            .asText()
            );


        }



        return result;

    }

}