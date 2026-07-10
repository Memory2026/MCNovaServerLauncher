package com.xingci.mcnsl.minecraft.download;


import com.xingci.mcnsl.minecraft.version.MinecraftLibrary;
import com.xingci.mcnsl.minecraft.version.MinecraftVersion;


import org.springframework.stereotype.Component;


import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;



/**
 * Minecraft Libraries 下载器
 *
 * 下载:
 *
 * libraries/*.jar
 */
@Component
public class LibraryDownloader {



    private final HttpClient client =

            HttpClient.newHttpClient();









    /**
     * 下载Libraries
     */
    public void downloadLibraries(
            MinecraftVersion version,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {



        if(version.getLibraries()==null){


            return;


        }








        int total =

                version.getLibraries()
                        .size();



        int current = 0;








        for(MinecraftLibrary library :
                version.getLibraries()){



            current++;





            /*
             * 判断系统规则
             *
             * 后续可扩展
             */
            if(library.getUrl()==null
                    ||
                    library.getUrl().isBlank()){


                continue;


            }







            Path target =

                    resolvePath(

                            library,

                            gameDir

                    );







            if(target==null){


                continue;


            }







            Files.createDirectories(

                    target.getParent()

            );








            task.setStep(

                    "下载Libraries"

            );




            task.setCurrentFile(

                    library.getName()

            );








            downloadFile(

                    library.getUrl(),

                    target

            );

            if(library.isNativeLibrary()){

                extractNative(
                        target,
                        gameDir.resolve("natives")
                );

            }








            task.update(

                    current,

                    total

            );


        }



    }









    private void extractNative(
            Path jar,
            Path target
    )
            throws IOException {

        Files.createDirectories(target);

        try(
                java.util.zip.ZipInputStream zip =
                        new java.util.zip.ZipInputStream(
                                Files.newInputStream(jar)
                        )
        ){

            java.util.zip.ZipEntry entry;

            while((entry = zip.getNextEntry()) != null){

                if(entry.isDirectory()
                        ||
                        entry.getName().contains("META-INF")) {

                    continue;

                }

                String fileName =
                        Path.of(entry.getName())
                                .getFileName()
                                .toString();

                if(!(fileName.endsWith(".dll")
                        ||
                        fileName.endsWith(".so")
                        ||
                        fileName.endsWith(".dylib"))) {

                    continue;

                }

                Files.copy(
                        zip,
                        target.resolve(fileName),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

            }

        }

    }









    /**
     * 解析library路径
     */
    private Path resolvePath(
            MinecraftLibrary library,
            Path gameDir
    ){



        if(library.getPath()!=null
                &&
                !library.getPath().isBlank()){


            return gameDir

                    .resolve(
                            "libraries"
                    )

                    .resolve(
                            library.getPath()
                    );


        }






        String name =
                library.getName();





        if(name==null){

            return null;

        }







        String[] parts =
                name.split(":");




        if(parts.length < 3){

            return null;

        }







        String group =

                parts[0]
                        .replace(
                                ".",
                                "/"
                        );



        String artifact =
                parts[1];



        String version =
                parts[2];








        return gameDir

                .resolve(
                        "libraries"
                )

                .resolve(
                        group
                )

                .resolve(
                        artifact
                )

                .resolve(
                        version
                )

                .resolve(

                        artifact
                                +
                                "-"
                                +
                                version
                                +
                                ".jar"

                );


    }









    /**
     * 下载文件
     */
    private void downloadFile(
            String url,
            Path target
    )
            throws IOException, InterruptedException {



        HttpRequest request =

                HttpRequest.newBuilder()

                        .uri(
                                URI.create(url)
                        )

                        .GET()

                        .build();








        HttpResponse<byte[]> response =

                client.send(

                        request,

                        HttpResponse.BodyHandlers.ofByteArray()

                );








        Files.write(

                target,

                response.body()

        );


    }


}
