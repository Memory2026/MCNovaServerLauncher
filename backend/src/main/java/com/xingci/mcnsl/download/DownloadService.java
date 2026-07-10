package com.xingci.mcnsl.download;


import com.xingci.mcnsl.controller.DownloadController.InstallRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;


@Service
public class DownloadService {

    private final HttpClient client =
            HttpClient.newHttpClient();


    /**
     * 创建 Minecraft 下载任务
     *
     * @param request 下载请求
     * @return 任务信息
     */
    public Map<String,Object> install(
            InstallRequest request
    ){


        String taskId = UUID.randomUUID().toString();



        System.out.println("==============================");
        System.out.println("Minecraft 下载任务创建");
        System.out.println("任务ID: " + taskId);

        System.out.println(
                "Minecraft版本: "
                        + request.getVersionId()
        );


        System.out.println(
                "版本类型: "
                        + request.getType()
        );


        System.out.println(
                "加载器: "
                        + request.getLoaderType()
        );


        System.out.println(
                "加载器版本: "
                        + request.getLoaderVersion()
        );


        System.out.println(
                "Fabric API: "
                        + request.getFabricApiVersion()
        );


        System.out.println("==============================");



        /*
         *
         * 后续真正下载逻辑：
         *
         * 1. 下载 version.json
         *
         * 2. 下载 client.jar
         *
         * 3. 下载 libraries
         *
         * 4. 安装 Forge / NeoForge
         *
         * 5. 安装 Fabric Loader
         *
         * 6. 下载 Fabric API
         *
         * 7. 创建实例目录
         *
         */


        return Map.of(

                "code",
                0,

                "message",
                "download task created",

                "taskId",
                taskId

        );


    }

    public void download(
            String name,
            String url,
            Path target
    )
            throws IOException, InterruptedException {

        if(url == null
                ||
                url.isBlank()) {

            throw new IOException("下载地址为空: " + name);

        }

        Files.createDirectories(
                target.getParent()
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<byte[]> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofByteArray()
                );

        if(response.statusCode() < 200
                ||
                response.statusCode() >= 300) {

            throw new IOException(
                    name + " 下载失败: HTTP " + response.statusCode()
            );

        }

        Files.write(
                target,
                response.body()
        );

    }


}
