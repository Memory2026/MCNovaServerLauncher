package com.xingci.mcnsl.minecraft.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class OptiFineInstaller {

    private static final String OPTIFINE_BASE_URL = "https://optifine.net";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public void install(String minecraftVersion, String loaderVersion, Path gameDir) throws Exception {
        String versionName = minecraftVersion + "_" + loaderVersion;
        String downloadUrl = String.format(
                "%s/download?f=OptiFine_%s.jar",
                OPTIFINE_BASE_URL, versionName
        );

        Path optifineJar = gameDir.resolve("mods").resolve("OptiFine_" + versionName + ".jar");
        Files.createDirectories(optifineJar.getParent());
        downloadFile(downloadUrl, optifineJar);

        Path versionsDir = gameDir.resolve("versions").resolve(minecraftVersion + "-optifine-" + loaderVersion);
        Files.createDirectories(versionsDir);

        Path vanillaJson = gameDir.resolve("versions").resolve(minecraftVersion).resolve(minecraftVersion + ".json");
        if (Files.exists(vanillaJson)) {
            JsonNode json = mapper.readTree(vanillaJson.toFile());
            ObjectNode obj = (ObjectNode) json;
            obj.put("id", minecraftVersion + "-optifine-" + loaderVersion);
            obj.put("inheritsFrom", minecraftVersion);
            mapper.writeValue(versionsDir.resolve(minecraftVersion + "-optifine-" + loaderVersion + ".json").toFile(), json);
        }

        Path versionJar = versionsDir.resolve(minecraftVersion + "-optifine-" + loaderVersion + ".jar");
        if (!Files.exists(versionJar)) {
            Files.createFile(versionJar);
        }
    }

    private void downloadFile(String url, Path destination) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<java.io.InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream is = response.body()) {
            Files.copy(is, destination);
        }
    }

}