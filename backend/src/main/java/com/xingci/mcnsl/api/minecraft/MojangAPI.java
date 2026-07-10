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
public class MojangAPI {


    private static final String API =
            "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";


    private final HttpClient client =
            HttpClient.newHttpClient();


    private final ObjectMapper mapper =
            new ObjectMapper();



    public List<String> getVersions()
            throws IOException, InterruptedException {


        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(API))
                        .GET()
                        .build();



        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );



        JsonNode root =
                mapper.readTree(response.body());



        List<String> result =
                new ArrayList<>();



        for(JsonNode node :
                root.path("versions")){


            result.add(
                    node.path("id")
                            .asText()
            );

        }


        return result;

    }

}