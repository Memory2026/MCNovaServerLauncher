package com.xingci.mcnsl.minecraft.launch;


import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;


/**
 * Minecraft Native解析器
 *
 * 负责：
 *
 * 根据系统选择正确 natives classifier
 */
@Component
public class NativeResolver {



    /**
     * 获取当前系统对应的native key
     */
    public String getNativeKey(){


        String os =
                System.getProperty(
                                "os.name"
                        )
                        .toLowerCase();




        if(os.contains("win")){


            return "natives-windows";


        }



        if(os.contains("mac")){


            return "natives-osx";


        }



        if(os.contains("linux")){


            return "natives-linux";


        }



        return "natives-linux";


    }









    /**
     * 获取需要解压的Native库
     */
    public List<String> resolve(
            JsonNode json
    ){


        List<String> result =
                new ArrayList<>();



        String nativeKey =
                getNativeKey();




        JsonNode libraries =
                json.path("libraries");



        if(!libraries.isArray()){

            return result;

        }






        for(JsonNode library : libraries){



            JsonNode classifiers =
                    library
                            .path("downloads")
                            .path("classifiers");





            if(!classifiers.isObject()){


                continue;

            }






            JsonNode nativeJar =
                    classifiers.path(
                            nativeKey
                    );





            if(!nativeJar.isMissingNode()){


                String path =
                        nativeJar
                                .path("path")
                                .asText();




                if(!path.isBlank()){


                    result.add(
                            path
                    );


                }


            }



        }





        return result;


    }



}