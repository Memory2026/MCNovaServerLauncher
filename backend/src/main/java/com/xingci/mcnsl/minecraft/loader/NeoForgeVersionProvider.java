package com.xingci.mcnsl.minecraft.loader;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;



@Component("minecraftNeoForgeVersionProvider")
public class NeoForgeVersionProvider {



    private static final String MAVEN_URL =
            "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml";




    /**
     * 搜索 NeoForge
     */
    public List<LoaderVersion> search(
            String minecraftVersion
    ){


        List<LoaderVersion> result =
                new ArrayList<>();


        try {


            HttpClient client =
                    HttpClient.newHttpClient();



            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            MAVEN_URL
                                    )
                            )
                            .GET()
                            .build();



            String xml =
                    client.send(
                                    request,
                                    HttpResponse.BodyHandlers.ofString()
                            )
                            .body();





            /*
             * 简单解析
             *
             * <version>
             * 21.0.143
             * </version>
             */


            String[] versions =
                    xml.split("<version>");



            for(String item:versions){


                if(!item.contains("</version>")){
                    continue;
                }


                String version =
                        item.substring(
                                0,
                                item.indexOf("</version>")
                        );



                /*
                 * NeoForge规则:
                 *
                 * 1.20.6 -> 20.x
                 * 1.21 -> 21.x
                 */


                if(matchMinecraft(
                        version,
                        minecraftVersion
                )){


                    result.add(

                            new LoaderVersion(
                                    "NeoForge",
                                    minecraftVersion,
                                    version,
                                    "https://maven.neoforged.net/releases/net/neoforged/neoforge/"
                                            +version
                            )

                    );


                }


            }


        }
        catch(Exception e){


            e.printStackTrace();


        }



        return result;


    }






    private boolean matchMinecraft(
            String loader,
            String mc
    ){


        if(mc.startsWith("1.21")){


            return loader.startsWith("21.");

        }


        if(mc.startsWith("1.20")){


            return loader.startsWith("20.");

        }


        return false;


    }



}
