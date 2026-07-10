package com.xingci.mcnsl.manager.loader;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingci.mcnsl.model.LoaderVersionInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Component
public class ForgeVersionProvider {


    private static final String PROMOS_URL =
            "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";


    private static final String MAVEN_URL =
            "https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml";


    private final RestTemplate restTemplate =
            new RestTemplate();


    private final ObjectMapper mapper =
            new ObjectMapper();


    public List<LoaderVersionInfo> findVersions(
            String mcVersion
    ){

        List<LoaderVersionInfo> result =
                new ArrayList<>();


        fetchFromMaven(mcVersion, result);


        fetchFromPromos(mcVersion, result);


        result.sort(
                Comparator.comparing(
                        LoaderVersionInfo::getVersion,
                        Comparator.reverseOrder()
                )
        );


        return result;

    }


    private void fetchFromMaven(
            String mcVersion,
            List<LoaderVersionInfo> result
    ){

        try {


            String xml =
                    restTemplate.getForObject(
                            MAVEN_URL,
                            String.class
                    );


            if(xml == null)
                return;


            String[] versions =
                    xml.split("<version>");


            for(String v:versions){


                if(v.contains("</version>")){


                    String version =
                            v.substring(
                                    0,
                                    v.indexOf("</version>")
                            );


                    if(version.startsWith(mcVersion + "-")){


                        String forgeVersion =
                                version.substring(
                                        mcVersion.length() + 1
                                );


                        LoaderVersionInfo info =
                                new LoaderVersionInfo(
                                        "forge",
                                        forgeVersion,
                                        mcVersion
                                );


                        result.add(info);

                    }

                }

            }


        }catch(Exception e){

            e.printStackTrace();

        }

    }


    private void fetchFromPromos(
            String mcVersion,
            List<LoaderVersionInfo> result
    ){

        try {


            String json =
                    restTemplate.getForObject(
                            PROMOS_URL,
                            String.class
                    );


            JsonNode root =
                    mapper.readTree(json);


            JsonNode promos =
                    root.get("promos");


            promos.fieldNames()
                    .forEachRemaining(key -> {


                        if(key.startsWith(mcVersion)){

                            String forgeVersion =
                                    promos.get(key).asText();


                            result.stream()
                                    .filter(info -> info.getVersion().equals(forgeVersion))
                                    .findFirst()
                                    .ifPresent(info -> {

                                        if(key.endsWith("-recommended")){
                                            info.setRecommended(true);
                                        }

                                    });
                        }

                    });


        }catch(Exception e){

            e.printStackTrace();

        }

    }

}