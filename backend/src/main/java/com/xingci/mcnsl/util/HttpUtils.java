package com.xingci.mcnsl.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * HTTP 工具类
 */
public final class HttpUtils {

    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .version(HttpClient.Version.HTTP_2)
                    .build();

    private HttpUtils() {
    }

    /**
     * GET 请求，返回字符串
     */
    public static String get(String url)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "MCNovaServerLauncher/1.0")
                .GET()
                .build();

        HttpResponse<String> response =
                CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException(
                    "HTTP " + response.statusCode() + " : " + url
            );
        }

        return response.body();
    }

    /**
     * GET 请求
     *
     * 返回字节数组
     */
    public static byte[] getBytes(String url)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "MCNovaServerLauncher/1.0")
                .GET()
                .build();

        HttpResponse<byte[]> response =
                CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException(
                    "HTTP " + response.statusCode() + " : " + url
            );
        }

        return response.body();
    }

    /**
     * 下载文件
     */
    public static void download(String url, Path target)
            throws IOException, InterruptedException {

        Files.createDirectories(target.getParent());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(10))
                .header("User-Agent", "MCNovaServerLauncher/1.0")
                .GET()
                .build();

        HttpResponse<Path> response =
                CLIENT.send(request,
                        HttpResponse.BodyHandlers.ofFile(target));

        if (response.statusCode() != 200) {

            Files.deleteIfExists(target);

            throw new IOException(
                    "Download failed : HTTP "
                            + response.statusCode()
            );
        }
    }

    /**
     * 文件是否存在
     */
    public static boolean exists(String url) {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "MCNovaServerLauncher/1.0")
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response =
                    CLIENT.send(request,
                            HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;

        } catch (Exception e) {
            return false;
        }
    }

}