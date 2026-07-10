package com.xingci.mcnsl.minecraft.launch;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Minecraft版本解析器
 *
 * 负责：
 *
 * 1. 读取version.json
 * 2. 处理inheritsFrom
 * 3. 合并父版本配置
 */
@Component
public class VersionResolver {


    private final ObjectMapper mapper =
            new ObjectMapper();





    /**
     * 解析版本
     *
     * @param gameDir .minecraft目录
     * @param version 版本ID
     */
    public JsonNode resolve(
            Path gameDir,
            String version
    )
            throws IOException {



        Path versionFile =
                gameDir
                        .resolve("versions")
                        .resolve(version)
                        .resolve(
                                version
                                        +
                                        ".json"
                        );



        if(!Files.exists(versionFile)){


            throw new IOException(
                    "版本文件不存在:"
                            +
                            versionFile
            );


        }




        JsonNode current =
                mapper.readTree(
                        versionFile.toFile()
                );





        return resolveParent(
                gameDir,
                current
        );


    }









    /**
     * 递归处理继承
     */
    private JsonNode resolveParent(
            Path gameDir,
            JsonNode child
    )
            throws IOException {



        String parent =
                child.path(
                                "inheritsFrom"
                        )
                        .asText();





        if(parent.isBlank()){


            return child;


        }








        Path parentFile =
                gameDir
                        .resolve("versions")
                        .resolve(parent)
                        .resolve(
                                parent
                                        +
                                        ".json"
                        );






        if(!Files.exists(parentFile)){


            throw new IOException(
                    "父版本不存在:"
                            +
                            parent
            );


        }





        JsonNode parentJson =
                mapper.readTree(
                        parentFile.toFile()
                );







        /*
         * 继续处理父版本继承
         */
        parentJson =
                resolveParent(
                        gameDir,
                        parentJson
                );







        return merge(
                parentJson,
                child
        );


    }









    /**
     * 合并版本JSON
     */
    private JsonNode merge(
            JsonNode parent,
            JsonNode child
    ){



        ObjectNode result =
                parent.deepCopy();





        child.fields()
                .forEachRemaining(entry -> {


                    String key =
                            entry.getKey();



                    JsonNode value =
                            entry.getValue();





                    /*
                     * libraries合并
                     */
                    if("libraries".equals(key)
                            &&
                            value.isArray()){


                        ArrayNode libraries =
                                result.withArray(
                                        "libraries"
                                );


                        for(JsonNode lib:value){


                            libraries.add(
                                    lib
                            );


                        }


                        return;

                    }







                    /*
                     * arguments合并
                     */
                    if("arguments".equals(key)
                            &&
                            value.isObject()){


                        ObjectNode args =
                                result.with(
                                        "arguments"
                                );



                        value.fields()
                                .forEachRemaining(
                                        arg -> {


                                            String argKey =
                                                    arg.getKey();



                                            JsonNode argValue =
                                                    arg.getValue();



                                            if(args.has(argKey)
                                                    &&
                                                    args.get(argKey)
                                                            .isArray()){


                                                ArrayNode arr =
                                                        (ArrayNode)
                                                                args.get(argKey);



                                                for(JsonNode item:
                                                        argValue){


                                                    arr.add(
                                                            item
                                                    );


                                                }


                                            }
                                            else{


                                                args.set(
                                                        argKey,
                                                        argValue
                                                );


                                            }


                                        });


                        return;

                    }








                    /*
                     * 普通字段覆盖
                     */
                    result.set(
                            key,
                            value
                    );


                });





        result.remove(
                "inheritsFrom"
        );



        return result;


    }


}