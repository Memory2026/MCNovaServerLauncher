package com.xingci.mcnsl.minecraft.loader;


import com.xingci.mcnsl.minecraft.model.LoaderVersion;

import org.springframework.stereotype.Component;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;



@Component("minecraftForgeVersionProvider")
public class ForgeVersionProvider {



    private static final String MAVEN_URL =

            "https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml";



    private final HttpClient client =
            HttpClient.newHttpClient();




    /**
     * 获取Forge版本
     *
     * @param minecraftVersion Minecraft版本
     */
    public List<LoaderVersion> getVersions(
            String minecraftVersion
    ){


        List<LoaderVersion> result =
                new ArrayList<>();


        try {



            HttpRequest request =

                    HttpRequest.newBuilder()

                            .uri(
                                    URI.create(
                                            MAVEN_URL
                                    )
                            )

                            .GET()

                            .build();




            HttpResponse<String> response =

                    client.send(

                            request,

                            HttpResponse.BodyHandlers.ofString()

                    );





            String xml =
                    response.body();





            /*
             *
             * 示例:
             *
             * 1.20.1-47.3.0
             *
             */



            String[] versions =

                    xml.split("<version>");





            for(String v:versions){


                if(!v.contains("</version>")){

                    continue;

                }





                String version =

                        v.substring(
                                0,
                                v.indexOf(
                                        "</version>"
                                )
                        );





                if(version.startsWith(
                        minecraftVersion
                )){


                    String url =

                            "https://maven.minecraftforge.net/net/minecraftforge/forge/"
                                    +
                                    version
                                    +
                                    "/forge-"
                                    +
                                    version
                                    +
                                    "-installer.jar";



                    result.add(

                            new LoaderVersion(

                                    minecraftVersion,

                                    "Forge",

                                    version,

                                    url

                            )

                    );


                }


            }





        }catch(Exception e){


            e.printStackTrace();


        }




        return result;


    }


}
