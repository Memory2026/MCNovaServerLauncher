package com.xingci.mcnsl.minecraft.download;


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
 * Minecraft客户端文件下载器
 *
 * 下载:
 *
 * versions/<version>/<version>.jar
 *
 */
@Component
public class ClientDownloader {



    private final HttpClient client =
            HttpClient.newHttpClient();








    /**
     * 下载客户端
     */
    public void downloadClient(
            MinecraftVersion version,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {



        String url =
                version.getClientUrl();





        if(url == null
                ||
                url.isBlank()){


            throw new RuntimeException(
                    "客户端下载地址为空"
            );


        }







        Path versionDir =

                gameDir

                        .resolve("versions")

                        .resolve(
                                version.getId()
                        );






        Files.createDirectories(
                versionDir
        );








        Path target =

                versionDir

                        .resolve(
                                version.getId()
                                        +
                                        ".jar"
                        );







        task.setStep(
                "下载客户端"
        );



        task.setCurrentFile(

                target.getFileName()
                        .toString()

        );








        downloadFile(

                url,

                target,

                task,

                version.getClientSize()

        );







    }









    /**
     * 下载文件
     */
    private void downloadFile(
            String url,
            Path target,
            MinecraftInstallTask task,
            long totalSize
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







        byte[] data =

                response.body();







        Files.write(

                target,

                data

        );






        task.update(

                data.length,

                totalSize

        );



    }


}