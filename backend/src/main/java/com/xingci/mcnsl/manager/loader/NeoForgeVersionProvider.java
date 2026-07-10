package com.xingci.mcnsl.manager.loader;


import com.xingci.mcnsl.model.LoaderVersionInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Component
public class NeoForgeVersionProvider {


    private final RestTemplate rest =
            new RestTemplate();


    public List<LoaderVersionInfo> findVersions(
            String mcVersion
    ){

        List<LoaderVersionInfo> list =
                new ArrayList<>();

        String url =
                "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml";

        try {


            String xml =
                    rest.getForObject(
                            url,
                            String.class
                    );


            if(xml == null)
                return list;


            String[] versions =
                    xml.split("<version>");


            for(String v:versions){


                if(v.contains("</version>")){


                    String version =
                            v.substring(
                                    0,
                                    v.indexOf("</version>")
                            );


                    if(version.startsWith(mcVersion)){


                        LoaderVersionInfo info =
                                new LoaderVersionInfo(
                                        "neoforge",
                                        version,
                                        mcVersion
                                );


                        list.add(info);

                    }

                }

            }


        }catch(Exception e){

            e.printStackTrace();

        }


        list.sort(
                Comparator.comparing(
                        LoaderVersionInfo::getVersion,
                        Comparator.reverseOrder()
                )
        );


        return list;

    }

}