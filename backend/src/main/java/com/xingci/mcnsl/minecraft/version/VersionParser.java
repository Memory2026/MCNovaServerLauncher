package com.xingci.mcnsl.minecraft.version;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.stereotype.Component;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



/**
 * Minecraft版本解析器
 *
 * version.json
 *
 * ->
 *
 * MinecraftVersion
 */
@Component
public class VersionParser {



    private final ObjectMapper mapper =
            new ObjectMapper();









    /**
     * 解析version.json
     */
    public MinecraftVersion parse(
            Path versionFile,
            Path gameDir
    )
            throws Exception {



        JsonNode json =
                mapper.readTree(
                        Files.readString(versionFile)
                );

        MinecraftVersion version =
                parseJson(json);

        String parent =
                json.path("inheritsFrom")
                        .asText();

        if(parent != null
                &&
                !parent.isBlank()) {

            Path parentFile =
                    gameDir.resolve("versions")
                            .resolve(parent)
                            .resolve(parent + ".json");

            if(!Files.exists(parentFile)) {

                throw new RuntimeException("缺少父版本描述文件: " + parent);

            }

            MinecraftVersion parentVersion =
                    parse(
                            parentFile,
                            gameDir
                    );

            return mergeInherited(
                    parentVersion,
                    version
            );

        }

        return version;

    }









    private MinecraftVersion parseJson(
            JsonNode json
    ){





        MinecraftVersion version =

                new MinecraftVersion();





        /*
         * 基础信息
         */
        version.setId(

                json.path("id")
                        .asText()

        );

        version.setClientJarVersion(
                version.getId()
        );


        version.setType(

                json.path("type")
                        .asText()

        );


        version.setReleaseTime(

                json.path("releaseTime")
                        .asText()

        );



        version.setMainClass(

                json.path("mainClass")
                        .asText()

        );








        /*
         * Asset Index
         */
        JsonNode assetIndex =

                json.path(
                        "assetIndex"
                );




        version.setAssetIndexId(

                assetIndex.path("id")
                        .asText()

        );



        version.setAssetIndexUrl(

                assetIndex.path("url")
                        .asText()

        );



        version.setAssetIndexSha1(

                assetIndex.path("sha1")
                        .asText()

        );









        /*
         * Client
         */
        JsonNode downloads =

                json.path(
                        "downloads"
                );



        JsonNode client =

                downloads.path(
                        "client"
                );




        version.setClientUrl(

                client.path("url")
                        .asText()

        );



        version.setClientSha1(

                client.path("sha1")
                        .asText()

        );



        version.setClientSize(

                client.path("size")
                        .asLong()

        );









        /*
         * Java版本
         */
        JsonNode java =

                json.path(
                        "javaVersion"
                );



        version.setJavaVersion(

                java.path(
                                "majorVersion"
                        )
                        .asInt()

        );









        /*
         * Libraries
         */
        version.setLibraries(

                parseLibraries(
                        json
                )

        );








        /*
         * 参数
         */
        parseArguments(

                json,

                version

        );





        return version;


    }









    private MinecraftVersion mergeInherited(
            MinecraftVersion parent,
            MinecraftVersion child
    ){

        if(child.getType() == null
                ||
                child.getType().isBlank()) {

            child.setType(parent.getType());

        }

        if(child.getReleaseTime() == null
                ||
                child.getReleaseTime().isBlank()) {

            child.setReleaseTime(parent.getReleaseTime());

        }

        if(child.getMainClass() == null
                ||
                child.getMainClass().isBlank()) {

            child.setMainClass(parent.getMainClass());

        }

        child.setAssetIndexId(parent.getAssetIndexId());
        child.setAssetIndexUrl(parent.getAssetIndexUrl());
        child.setAssetIndexSha1(parent.getAssetIndexSha1());
        child.setClientUrl(parent.getClientUrl());
        child.setClientSha1(parent.getClientSha1());
        child.setClientSize(parent.getClientSize());
        child.setClientJarVersion(parent.getClientJarVersion());
        child.setJavaVersion(parent.getJavaVersion());

        List<MinecraftLibrary> libraries =
                new ArrayList<>();

        if(parent.getLibraries() != null) {

            libraries.addAll(parent.getLibraries());

        }

        if(child.getLibraries() != null) {

            libraries.addAll(child.getLibraries());

        }

        child.setLibraries(libraries);

        if(child.getJvmArguments() == null
                ||
                child.getJvmArguments().isEmpty()) {

            child.setJvmArguments(parent.getJvmArguments());

        }

        if(child.getGameArguments() == null
                ||
                child.getGameArguments().isEmpty()) {

            child.setGameArguments(parent.getGameArguments());

        }

        return child;

    }









    /**
     * 解析Libraries
     */
    private List<MinecraftLibrary> parseLibraries(
            JsonNode json
    ){


        List<MinecraftLibrary> list =

                new ArrayList<>();





        JsonNode libraries =

                json.path(
                        "libraries"
                );





        if(!libraries.isArray()){

            return list;

        }







        for(JsonNode node:libraries){



            MinecraftLibrary library =

                    new MinecraftLibrary();





            library.setName(

                    node.path("name")
                            .asText()

            );





            JsonNode artifact =

                    node

                            .path("downloads")

                            .path("artifact");






            library.setPath(

                    artifact.path("path")
                            .asText()

            );



            library.setUrl(

                    artifact.path("url")
                            .asText()

            );



            library.setSha1(

                    artifact.path("sha1")
                            .asText()

            );



            library.setSize(

                    artifact.path("size")
                            .asLong()

            );



            list.add(
                    library
            );

            MinecraftLibrary nativeLibrary =
                    parseNativeLibrary(node);

            if(nativeLibrary != null) {

                list.add(nativeLibrary);

            }

        }




        return list;


    }









    private MinecraftLibrary parseNativeLibrary(
            JsonNode node
    ){

        String classifierKey =
                currentNativeClassifier();

        JsonNode classifier =
                node.path("downloads")
                        .path("classifiers")
                        .path(classifierKey);

        if(classifier.isMissingNode()
                ||
                classifier.path("url").asText().isBlank()) {

            return null;

        }

        MinecraftLibrary library =
                new MinecraftLibrary();

        library.setName(
                node.path("name")
                        .asText()
        );

        library.setPath(
                classifier.path("path")
                        .asText()
        );

        library.setNativePath(
                classifier.path("path")
                        .asText()
        );

        library.setUrl(
                classifier.path("url")
                        .asText()
        );

        library.setSha1(
                classifier.path("sha1")
                        .asText()
        );

        library.setSize(
                classifier.path("size")
                        .asLong()
        );

        library.setNativeLibrary(true);

        return library;

    }









    private String currentNativeClassifier(){

        String os =
                System.getProperty("os.name")
                        .toLowerCase();

        String arch =
                System.getProperty("os.arch")
                        .toLowerCase();

        if(os.contains("win")) {

            return arch.contains("64")
                    ?
                    "natives-windows"
                    :
                    "natives-windows-x86";

        }

        if(os.contains("mac")) {

            return arch.contains("aarch64")
                    ||
                    arch.contains("arm64")
                    ?
                    "natives-macos-arm64"
                    :
                    "natives-macos";

        }

        return "natives-linux";

    }









    /**
     * 解析启动参数
     */
    private void parseArguments(
            JsonNode json,
            MinecraftVersion version
    ){



        List<String> jvm =

                new ArrayList<>();


        List<String> game =

                new ArrayList<>();







        JsonNode arguments =

                json.path(
                        "arguments"
                );





        if(arguments.isObject()){



            JsonNode jvmNode =

                    arguments.path(
                            "jvm"
                    );




            if(jvmNode.isArray()){


                for(JsonNode n:jvmNode){


                    if(n.isTextual()){

                        jvm.add(
                                n.asText()
                        );

                    }

                }

            }







            JsonNode gameNode =

                    arguments.path(
                            "game"
                    );




            if(gameNode.isArray()){


                for(JsonNode n:gameNode){


                    if(n.isTextual()){

                        game.add(
                                n.asText()
                        );

                    }

                }


            }


        }






        version.setJvmArguments(
                jvm
        );


        version.setGameArguments(
                game
        );


    }



}
