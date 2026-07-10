package com.xingci.mcnsl.minecraft.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class VersionManifestDownloader {

    private static final String MANIFEST_URL =
            "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

    private final ObjectMapper mapper =
            new ObjectMapper();

    private final HttpClient client =
            HttpClient.newHttpClient();

    public Path ensureVersionJson(
            String version,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {

        Path versionDir =
                gameDir.resolve("versions")
                        .resolve(version);

        Files.createDirectories(versionDir);

        Path versionJson =
                versionDir.resolve(version + ".json");

        if(Files.exists(versionJson)
                &&
                Files.size(versionJson) > 0) {

            return versionJson;

        }

        task.setStep("获取版本清单");
        task.setCurrentFile("version_manifest_v2.json");

        JsonNode manifest =
                getJson(MANIFEST_URL);

        String versionUrl =
                null;

        for(JsonNode node : manifest.path("versions")) {

            if(version.equals(node.path("id").asText())) {

                versionUrl =
                        node.path("url").asText();

                break;

            }

        }

        if(versionUrl == null
                ||
                versionUrl.isBlank()) {

            throw new IOException("未找到 Minecraft 版本: " + version);

        }

        task.setStep("下载版本描述");
        task.setCurrentFile(version + ".json");

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(versionUrl))
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if(response.statusCode() < 200
                ||
                response.statusCode() >= 300) {

            throw new IOException("版本描述下载失败: HTTP " + response.statusCode());

        }

        Files.writeString(versionJson, response.body());

        return versionJson;

    }

    private JsonNode getJson(String url)
            throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if(response.statusCode() < 200
                ||
                response.statusCode() >= 300) {

            throw new IOException("请求失败: HTTP " + response.statusCode());

        }

        return mapper.readTree(response.body());

    }

}
