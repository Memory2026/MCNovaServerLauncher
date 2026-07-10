package com.xingci.mcnsl.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.*;
import java.util.*;



@RestController
@RequestMapping("/api/minecraft")
@CrossOrigin("*")
public class VersionController {

    private volatile List<Map<String,Object>> cachedVersions =
            fallbackVersions();


    private final ObjectMapper mapper =
            new ObjectMapper();



    private final HttpClient client =
            HttpClient.newHttpClient();




    @GetMapping("/versions")
    public List<Map<String,Object>> getVersions()
            throws Exception {

        try {


        HttpRequest request =
                HttpRequest.newBuilder()

                        .uri(
                                URI.create(
                                        "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
                                )
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




            List<Map<String,Object>> result =
                    new ArrayList<>();




            for(JsonNode node :
                    root.path("versions")) {



                Map<String,Object> version =
                        new HashMap<>();


                version.put(
                        "id",
                        node.path("id").asText()
                );


                version.put(
                        "versionId",
                        node.path("id").asText()
                );


                version.put(
                        "name",
                        node.path("id").asText()
                );


                version.put(
                        "type",
                        node.path("type").asText()
                );


                version.put(
                        "releaseTime",
                        node.path("releaseTime").asText()
                );


                version.put(
                        "isInstalled",
                        false
                );


                result.add(version);

            }

            if(!result.isEmpty()) {

                cachedVersions =
                        result;

            }

            return cachedVersions;

        }
        catch(Exception e) {

            return cachedVersions;

        }

    }

    private static List<Map<String,Object>> fallbackVersions(){

        List<Map<String,Object>> versions =
                new ArrayList<>();

        addFallback(versions, "1.21.8", "release", "2025-07-17T12:00:00+00:00");
        addFallback(versions, "1.21.7", "release", "2025-06-30T12:00:00+00:00");
        addFallback(versions, "1.21.6", "release", "2025-06-17T12:00:00+00:00");
        addFallback(versions, "1.21.5", "release", "2025-03-25T12:00:00+00:00");
        addFallback(versions, "1.21.4", "release", "2024-12-03T12:00:00+00:00");
        addFallback(versions, "1.21.1", "release", "2024-08-08T12:00:00+00:00");
        addFallback(versions, "1.20.6", "release", "2024-04-29T12:00:00+00:00");
        addFallback(versions, "1.20.4", "release", "2023-12-07T12:00:00+00:00");
        addFallback(versions, "1.20.1", "release", "2023-06-12T12:00:00+00:00");
        addFallback(versions, "1.19.4", "release", "2023-03-14T12:00:00+00:00");
        addFallback(versions, "25w31a", "snapshot", "2025-07-30T12:00:00+00:00");
        addFallback(versions, "24w14potato", "april_fools", "2024-04-01T12:00:00+00:00");
        addFallback(versions, "1.2.5", "old_alpha", "2012-04-04T12:00:00+00:00");

        return versions;

    }

    private static void addFallback(
            List<Map<String,Object>> versions,
            String id,
            String type,
            String releaseTime
    ){

        Map<String,Object> version =
                new HashMap<>();

        version.put("id", id);
        version.put("versionId", id);
        version.put("name", id);
        version.put("type", type);
        version.put("releaseTime", releaseTime);
        version.put("isInstalled", false);
        version.put("fallback", true);

        versions.add(version);

    }


}
