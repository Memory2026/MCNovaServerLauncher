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
public class NeoForgeInstaller {

    private static final String NEOFORGE_MAVEN = "https://maven.neoforged.net";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public void install(String minecraftVersion, String loaderVersion, Path gameDir) throws Exception {
        String installerUrl = String.format(
                "%s/net/neoforged/neoforge/%s-%s/neoforge-%s-%s-installer.jar",
                NEOFORGE_MAVEN, minecraftVersion, loaderVersion, minecraftVersion, loaderVersion
        );

        Path installerJar = gameDir.resolve("neoforge-installer.jar");
        downloadFile(installerUrl, installerJar);

        Path tempDir = Files.createTempDirectory("neoforge-install");
        try {
            extractJar(installerJar, tempDir);

            Path universalJar = findUniversalJar(tempDir, minecraftVersion, loaderVersion);
            if (universalJar != null) {
                Path versionsDir = gameDir.resolve("versions").resolve(minecraftVersion + "-neoforge-" + loaderVersion);
                Files.createDirectories(versionsDir);
                Files.copy(universalJar, versionsDir.resolve(minecraftVersion + "-neoforge-" + loaderVersion + ".jar"));
            }

            Path jsonFile = findJsonFile(tempDir, minecraftVersion, loaderVersion);
            if (jsonFile != null) {
                JsonNode json = mapper.readTree(jsonFile.toFile());
                ObjectNode obj = (ObjectNode) json;
                obj.put("id", minecraftVersion + "-neoforge-" + loaderVersion);
                Path versionsDir = gameDir.resolve("versions").resolve(minecraftVersion + "-neoforge-" + loaderVersion);
                Files.createDirectories(versionsDir);
                mapper.writeValue(versionsDir.resolve(minecraftVersion + "-neoforge-" + loaderVersion + ".json").toFile(), json);
            }
        } finally {
            deleteDir(tempDir);
            Files.deleteIfExists(installerJar);
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

    private void extractJar(Path jarPath, Path destDir) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                Path entryPath = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream is = jar.getInputStream(entry)) {
                        Files.copy(is, entryPath);
                    }
                }
            }
        }
    }

    private Path findUniversalJar(Path dir, String mcVersion, String loaderVersion) throws IOException {
        return Files.list(dir)
                .filter(p -> p.getFileName().toString().contains("universal") &&
                        p.getFileName().toString().contains(mcVersion))
                .findFirst()
                .orElse(null);
    }

    private Path findJsonFile(Path dir, String mcVersion, String loaderVersion) throws IOException {
        return Files.list(dir)
                .filter(p -> p.getFileName().toString().endsWith(".json"))
                .findFirst()
                .orElse(null);
    }

    private void deleteDir(Path dir) throws IOException {
        Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                    }
                });
    }

}