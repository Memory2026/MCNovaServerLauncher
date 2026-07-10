package com.xingci.mcnsl.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Minecraft Loader 版本服务
 */
@Service
public class LoaderVersionService {

    private static final Logger log =
            LoggerFactory.getLogger(LoaderVersionService.class);

    private static final String FABRIC_API_MAVEN_URL =
            "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Forge
     */
    public List<String> getForgeVersions(String mcVersion) {

        List<String> versions = new ArrayList<>();

        try {

            if (mcVersion.startsWith("1.21")) {

                versions.add("57.0.3");
                versions.add("57.0.2");
                versions.add("57.0.1");

            } else if (mcVersion.startsWith("1.20.6")) {

                versions.add("50.1.17");
                versions.add("50.1.15");

            } else if (mcVersion.startsWith("1.20.4")) {

                versions.add("49.1.0");
                versions.add("49.0.50");

            } else if (mcVersion.startsWith("1.20.1")) {

                versions.add("47.3.12");
                versions.add("47.3.10");
                versions.add("47.2.20");

            } else if (mcVersion.startsWith("1.19")) {

                versions.add("45.3.0");
                versions.add("45.2.0");

            }

        } catch (Exception e) {

            log.error("获取 Forge 版本失败", e);

        }

        return versions;
    }

    /**
     * NeoForge
     */
    public List<String> getNeoForgeVersions(String mcVersion) {

        List<String> versions = new ArrayList<>();

        try {

            if (mcVersion.startsWith("1.21.8")) {

                versions.add("21.8.45");

            } else if (mcVersion.startsWith("1.21.6")) {

                versions.add("21.6.32");

            } else if (mcVersion.startsWith("1.21.5")) {

                versions.add("21.5.30");

            } else if (mcVersion.startsWith("1.21.4")) {

                versions.add("21.4.148");
                versions.add("21.4.120");

            } else if (mcVersion.startsWith("1.21.1")) {

                versions.add("21.1.172");
                versions.add("21.1.150");

            } else if (mcVersion.startsWith("1.20.6")) {

                versions.add("20.6.124");

            } else if (mcVersion.startsWith("1.20.4")) {

                versions.add("20.4.237");

            }

        } catch (Exception e) {

            log.error("获取 NeoForge 版本失败", e);

        }

        return versions;
    }

    /**
     * Fabric Loader - 从 Fabric Meta API 动态获取，格式对齐 PCL 启动器
     */
    public List<String> getFabricVersions(String mcVersion) {

        List<String> versions = new ArrayList<>();

        try {

            String url = "https://meta.fabricmc.net/v2/versions/loader/" + mcVersion;
            String json = restTemplate.getForObject(url, String.class);

            if (json == null) {
                return getFallbackFabricVersions();
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode array = mapper.readTree(json);

            for (com.fasterxml.jackson.databind.JsonNode node : array) {
                String version = node.get("loader").get("version").asText();
                if (version != null && !version.isBlank()) {
                    versions.add(version);
                }
            }

            versions = versions.stream().distinct().sorted((v1, v2) -> {
                int[] arr1 = parseVersion(v1);
                int[] arr2 = parseVersion(v2);
                for (int i = 0; i < 3; i++) {
                    if (arr1[i] != arr2[i]) {
                        return Integer.compare(arr2[i], arr1[i]);
                    }
                }
                return 0;
            }).toList();

        } catch (Exception e) {

            log.error("获取 Fabric Loader 失败，使用兜底数据", e);
            versions = getFallbackFabricVersions();

        }

        return versions;
    }

    private List<String> getFallbackFabricVersions() {
        return List.of("0.19.3", "0.19.2", "0.19.1", "0.19.0", "0.18.14");
    }

    /**
     * Fabric API - 从 Maven 仓库动态获取
     */
    public List<String> getFabricApiVersions(String mcVersion) {

        List<String> versions = new ArrayList<>();

        try {

            String xml = restTemplate.getForObject(FABRIC_API_MAVEN_URL, String.class);

            if (xml == null) {
                return getFallbackFabricApiVersions(mcVersion);
            }

            String[] parts = xml.split("<version>");
            for (String v : parts) {
                if (v.contains("</version>")) {
                    String version = v.substring(0, v.indexOf("</version>")).trim();

                    if (isValidFabricApiVersion(version, mcVersion)) {
                        versions.add(version);
                    }
                }
            }

            if (versions.isEmpty()) {
                String majorMinor = mcVersion;
                if (majorMinor.contains(".")) {
                    String[] split = majorMinor.split("\\.");
                    if (split.length >= 2) {
                        majorMinor = split[0] + "." + split[1];
                    }
                }
                for (String v : parts) {
                    if (v.contains("</version>")) {
                        String version = v.substring(0, v.indexOf("</version>")).trim();
                        if (version.contains("+" + majorMinor + ".") || version.contains("-" + majorMinor + ".")) {
                            if (!versions.contains(version)) {
                                versions.add(version);
                            }
                        }
                    }
                }
            }

            versions.sort(Comparator.reverseOrder());

        } catch (Exception e) {

            log.error("获取 Fabric API 失败，使用兜底数据", e);
            versions = getFallbackFabricApiVersions(mcVersion);

        }

        return versions;
    }

    private boolean isValidFabricApiVersion(String version, String mcVersion) {

        if (!version.contains("+") && !version.contains("-")) {
            return false;
        }

        String[] versionParts = version.split("[+-]");
        if (versionParts.length < 2) {
            return false;
        }

        String suffix = version.substring(version.indexOf(versionParts[versionParts.length - 1]));

        return suffix.startsWith(mcVersion)
                || suffix.startsWith("build." + mcVersion)
                || suffix.contains("-" + mcVersion)
                || suffix.contains("+" + mcVersion);
    }

    private List<String> getFallbackFabricApiVersions(String mcVersion) {

        List<String> versions = new ArrayList<>();

        switch (mcVersion) {
            case "1.21.8" -> {
                versions.add("0.133.4+1.21.8");
                versions.add("0.133.3+1.21.8");
            }
            case "1.21.6" -> {
                versions.add("0.132.0+1.21.6");
            }
            case "1.21.5" -> {
                versions.add("0.129.0+1.21.5");
            }
            case "1.21.4" -> {
                versions.add("0.119.2+1.21.4");
                versions.add("0.118.0+1.21.4");
            }
            case "1.21.1" -> {
                versions.add("0.116.4+1.21.1");
                versions.add("0.116.2+1.21.1");
            }
            case "1.20.6" -> {
                versions.add("0.100.8+1.20.6");
            }
            case "1.20.4" -> {
                versions.add("0.97.2+1.20.4");
            }
            case "1.20.1" -> {
                versions.add("0.92.5+1.20.1");
                versions.add("0.91.0+1.20.1");
            }
            default -> {
            }
        }

        return versions;
    }

    /**
     * OptiFine
     */
    public List<String> getOptifineVersions(String mcVersion) {

        List<String> versions = new ArrayList<>();

        try {

            if (mcVersion.startsWith("1.20.1")) {

                versions.add("HD_U_I6");
                versions.add("HD_U_I5");

            } else if (mcVersion.startsWith("1.19.4")) {

                versions.add("HD_U_I4");

            } else if (mcVersion.startsWith("1.19")) {

                versions.add("HD_U_H9");

            }

        } catch (Exception e) {

            log.error("获取 OptiFine 失败", e);

        }

        return versions;
    }

    /**
     * 通用入口
     */
    public List<String> getLoaderVersions(String loader, String mcVersion) {

        if (loader == null) {
            return Collections.emptyList();
        }

        return switch (loader.toLowerCase()) {

            case "forge" ->
                    getForgeVersions(mcVersion);

            case "neoforge" ->
                    getNeoForgeVersions(mcVersion);

            case "fabric" ->
                    getFabricVersions(mcVersion);

            case "optifine" ->
                    getOptifineVersions(mcVersion);

            default ->
                    Collections.emptyList();
        };
    }

    private int[] parseVersion(String version) {
        if (version == null || version.isBlank()) {
            return new int[]{0, 0, 0};
        }
        String[] parts = version.split("\\.");
        int[] result = new int[3];
        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            try {
                result[i] = Integer.parseInt(parts[i].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

}