package com.xingci.mcnsl.minecraft.launch;


import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;



/**
 * Minecraft 参数解析器
 *
 * 解析 version.json:
 *
 * arguments.jvm
 * arguments.game
 *
 */
@Component
public class ArgumentResolver {



    /**
     * JVM参数
     */
    public List<String> resolveJvmArguments(
            JsonNode json,
            LaunchOptions options
    ){


        return resolve(
                json.path("arguments")
                        .path("jvm"),
                options
        );


    }









    /**
     * 游戏参数
     */
    public List<String> resolveGameArguments(
            JsonNode json,
            LaunchOptions options
    ){


        return resolve(
                json.path("arguments")
                        .path("game"),
                options
        );


    }









    /**
     * 通用解析
     */
    private List<String> resolve(
            JsonNode node,
            LaunchOptions options
    ){


        List<String> result =
                new ArrayList<>();




        if(!node.isArray()){

            return result;

        }







        for(JsonNode arg : node){



            /*
             * 新版格式:
             *
             * {
             *   rules:[...],
             *   value:"xxx"
             * }
             */
            if(arg.isObject()){


                if(!allowed(arg, options)){


                    continue;


                }





                JsonNode value =
                        arg.path("value");




                if(value.isTextual()){


                    result.add(

                            replace(
                                    value.asText(),
                                    options
                            )

                    );


                }
                else if(value.isArray()){


                    for(JsonNode v:value){


                        result.add(

                                replace(
                                        v.asText(),
                                        options
                                )

                        );


                    }


                }


            }







            /*
             * 旧版格式
             */
            else if(arg.isTextual()){



                result.add(

                        replace(
                                arg.asText(),
                                options
                        )

                );


            }



        }




        return result;

    }









    /**
     * 判断rules
     */
    private boolean allowed(
            JsonNode argument,
            LaunchOptions options
    ){


        JsonNode rules =
                argument.path("rules");





        if(!rules.isArray()){


            return true;


        }






        boolean allow = false;





        for(JsonNode rule:rules){



            String action =
                    rule.path("action")
                            .asText();





            if("allow".equals(action)){


                allow = true;


            }





            JsonNode os =
                    rule.path("os");





            if(os.isObject()){



                String name =
                        os.path("name")
                                .asText();




                if(!matchOS(name)){


                    allow=false;


                }


            }





            JsonNode features =
                    rule.path("features");





            if(features.isObject()){



                if(features.has("is_demo_user")

                        &&

                        !options.isDemo()){



                    allow=false;


                }


            }




        }



        return allow;


    }









    /**
     * 系统判断
     */
    private boolean matchOS(
            String os
    ){



        String current =

                System.getProperty(
                                "os.name"
                        )
                        .toLowerCase();





        return switch(os){


            case "windows" ->

                    current.contains(
                            "win"
                    );



            case "linux" ->

                    current.contains(
                            "linux"
                    );



            case "osx" ->

                    current.contains(
                            "mac"
                    );



            default -> true;


        };


    }









    /**
     * 参数变量替换
     */
    private String replace(
            String value,
            LaunchOptions options
    ){



        return value



                .replace(
                        "${auth_player_name}",
                        options.getUsername()
                )



                .replace(
                        "${auth_uuid}",
                        options.getUuid()
                )



                .replace(
                        "${auth_access_token}",
                        options.getAccessToken()
                )



                .replace(
                        "${user_type}",
                        options.getUserType()
                )



                .replace(
                        "${user_properties}",
                        "{}"
                )



                .replace(
                        "${clientid}",
                        options.getClientId()==null
                                ?
                                ""
                                :
                                options.getClientId()
                )



                .replace(
                        "${version_name}",
                        options.getVersion()
                )



                .replace(
                        "${game_directory}",
                        options.getGameDir()
                                .toString()
                )



                .replace(
                        "${game_dir}",
                        options.getGameDir()
                                .toString()
                )



                .replace(
                        "${assets_root}",
                        options.getAssetsDir()!=null

                                ?

                                options.getAssetsDir()
                                .toString()

                                :

                                options.getGameDir()
                                .resolve("assets")
                                .toString()
                )



                .replace(
                        "${assets_index_name}",
                        options.getAssetIndex()==null

                                ?

                                options.getVersion()

                                :

                                options.getAssetIndex()
                );



    }



}