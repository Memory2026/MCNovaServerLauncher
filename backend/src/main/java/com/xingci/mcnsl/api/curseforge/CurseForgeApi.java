package com.xingci.mcnsl.api.curseforge;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingci.mcnsl.util.HttpUtils;

@Component
public class CurseForgeApi {

    private static final Logger logger = LoggerFactory.getLogger(CurseForgeApi.class);

    private static final String BASE = "https://api.curseforge.com/v1";
    private static final String API_KEY = "cfc_pat_5afea766fc7e001c8fabccfd$2a$10$GOiS5BaQtDDr0EdXDfe3m.mKM9niidgsfDm.Kisp7wURqCxe1hAfC";

    private final ObjectMapper mapper = new ObjectMapper();

    private boolean isApiKeyConfigured() {
        return API_KEY != null && !API_KEY.isBlank() && !API_KEY.equals("your-api-key-here");
    }

    public List<Map<String, Object>> searchMods(
            String query,
            int offset,
            int limit,
            String loader,
            String version
    ) throws Exception {

        if (!isApiKeyConfigured()) {
            logger.warn("CurseForge API Key 未配置");
            return new ArrayList<>();
        }

        StringBuilder url = new StringBuilder(BASE);
        url.append("/mods/search?gameId=432&index=").append(offset);
        url.append("&pageSize=").append(Math.min(limit, 50));

        if (query != null && !query.isBlank()) {
            url.append("&searchFilter=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        }

        if (version != null && !version.isBlank()) {
            url.append("&gameVersion=").append(URLEncoder.encode(version, StandardCharsets.UTF_8));
        }

        if (loader != null && !loader.isBlank()) {
            int categoryId = getLoaderCategoryId(loader);
            if (categoryId > 0) {
                url.append("&classId=6&categoryId=").append(categoryId);
            }
        }

        logger.info("CurseForge API 请求: {}", url);
        
        try {
            String json = HttpUtils.get(url.toString(), API_KEY);
            JsonNode root = mapper.readTree(json);

            List<Map<String, Object>> mods = new ArrayList<>();

            for (JsonNode hit : root.path("data")) {
                Map<String, Object> mod = new LinkedHashMap<>();

                String name = hit.path("name").asText();
                String slug = hit.path("slug").asText();
                String author = hit.path("authors").get(0).path("name").asText("Unknown");
                String description = hit.path("summary").asText();
                String iconUrl = hit.path("logo").path("url").asText();
                long downloads = hit.path("downloadCount").asLong();
                String updatedAt = hit.path("dateModified").asText();
                String projectId = hit.path("id").asText();

                mod.put("id", projectId);
                mod.put("slug", slug);
                mod.put("name", name);
                mod.put("author", author);
                mod.put("description", description);
                mod.put("icon", iconUrl);
                mod.put("downloads", formatDownloads(downloads));
                mod.put("updatedAt", formatRelativeTime(updatedAt));
                mod.put("platform", "curseforge");

                List<String> versions = new ArrayList<>();
                for (JsonNode gameVersion : hit.path("gameVersions")) {
                    String v = gameVersion.asText();
                    if (!isSnapshotVersion(v)) {
                        versions.add(v);
                    }
                }
                mod.put("versions", versions);

                List<String> categories = new ArrayList<>();
                for (JsonNode cat : hit.path("categories")) {
                    String catName = cat.path("name").asText();
                    categories.add(catName);
                }
                mod.put("categories", categories);

                mods.add(mod);
            }

            logger.info("CurseForge API 返回 {} 个模组", mods.size());
            return mods;
        } catch (Exception e) {
            logger.error("CurseForge API 请求失败: {}", e.getMessage());
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> errorMod = new LinkedHashMap<>();
            errorMod.put("id", "error");
            errorMod.put("name", "CurseForge API Key 无效");
            errorMod.put("author", "系统提示");
            errorMod.put("description", "请检查 CurseForge API Key 是否正确配置");
            errorMod.put("platform", "curseforge");
            result.add(errorMod);
            return result;
        }
    }

    public Map<String, Object> getProject(String projectId) throws Exception {
        if (!isApiKeyConfigured()) {
            Map<String, Object> mod = new LinkedHashMap<>();
            mod.put("error", "CurseForge API Key 未配置");
            return mod;
        }
        String url = BASE + "/mods/" + projectId;
        String json = HttpUtils.get(url, API_KEY);
        JsonNode root = mapper.readTree(json);

        JsonNode data = root.path("data");

        Map<String, Object> mod = new LinkedHashMap<>();

        String name = data.path("name").asText();
        String slug = data.path("slug").asText();
        String author = data.path("authors").get(0).path("name").asText("Unknown");
        String description = data.path("summary").asText();
        String iconUrl = data.path("logo").path("url").asText();
        long downloads = data.path("downloadCount").asLong();
        String updatedAt = data.path("dateModified").asText();

        mod.put("id", projectId);
        mod.put("slug", slug);
        mod.put("name", name);
        mod.put("author", author);
        mod.put("description", description);
        mod.put("icon", iconUrl);
        mod.put("downloads", formatDownloads(downloads));
        mod.put("updatedAt", formatRelativeTime(updatedAt));
        mod.put("platform", "curseforge");

        return mod;
    }

    public List<Map<String, Object>> getModVersions(String projectId) throws Exception {
        if (!isApiKeyConfigured()) {
            return new ArrayList<>();
        }
        String url = BASE + "/mods/" + projectId + "/files";
        String json = HttpUtils.get(url, API_KEY);
        JsonNode root = mapper.readTree(json);

        Map<String, Map<String, Object>> groups = new LinkedHashMap<>();

        for (JsonNode file : root.path("data")) {
            String fileName = file.path("fileName").asText();
            String fileUrl = file.path("downloadUrl").asText();
            long downloads = file.path("downloadCount").asLong();
            String datePublished = file.path("fileDate").asText();
            String versionType = file.path("releaseType").asText();
            boolean isBeta = !"release".equalsIgnoreCase(versionType);

            String versionNumber = extractVersionNumber(fileName);

            List<String> gameVersions = new ArrayList<>();
            for (JsonNode gv : file.path("gameVersions")) {
                String v = gv.asText();
                if (!isSnapshotVersion(v)) {
                    gameVersions.add(v);
                }
            }

            String loader = inferLoader(fileName);

            for (String gv : gameVersions) {
                String mcVersion = simplifyVersion(gv);
                String mainVersion = getMainVersion(mcVersion);
                String groupKey = mainVersion;

                if (!groups.containsKey(groupKey)) {
                    Map<String, Object> group = new LinkedHashMap<>();
                    group.put("title", mainVersion);
                    group.put("mcVersion", mainVersion);
                    group.put("subVersions", new LinkedHashMap<String, Map<String, Object>>());
                    groups.put(groupKey, group);
                }

                @SuppressWarnings("unchecked")
                Map<String, Map<String, Object>> subVersionsMap =
                        (Map<String, Map<String, Object>>) groups.get(groupKey).get("subVersions");

                if (!subVersionsMap.containsKey(mcVersion)) {
                    Map<String, Object> subVersionGroup = new LinkedHashMap<>();
                    subVersionGroup.put("title", mcVersion);
                    subVersionGroup.put("mcVersion", mcVersion);
                    subVersionGroup.put("loaders", new LinkedHashMap<String, List<Map<String, Object>>>());
                    subVersionsMap.put(mcVersion, subVersionGroup);
                }

                @SuppressWarnings("unchecked")
                Map<String, List<Map<String, Object>>> loadersMap =
                        (Map<String, List<Map<String, Object>>>) subVersionsMap.get(mcVersion).get("loaders");

                String loaderKey = loader.toLowerCase();
                if (!loadersMap.containsKey(loaderKey)) {
                    loadersMap.put(loaderKey, new ArrayList<>());
                }

                String loaderDisplay = formatLoaderName(loader);

                Map<String, Object> variant = new LinkedHashMap<>();
                variant.put("name", fileName);
                variant.put("versionNumber", versionNumber);
                variant.put("loader", loaderDisplay);
                variant.put("loaderKey", loaderKey);
                variant.put("mcVersion", mcVersion);
                variant.put("fileName", fileName);
                variant.put("fileUrl", fileUrl);
                variant.put("downloads", formatDownloads(downloads));
                variant.put("updatedAt", formatRelativeTime(datePublished));
                variant.put("isBeta", isBeta);
                variant.put("versionType", versionType);
                variant.put("featured", false);
                variant.put("icon", "");

                loadersMap.get(loaderKey).add(variant);
            }
        }

        return new ArrayList<>(groups.values());
    }

    private int getLoaderCategoryId(String loader) {
        String lower = loader.toLowerCase();
        if (lower.equals("fabric")) return 7492;
        if (lower.equals("forge")) return 7491;
        if (lower.equals("neoforge")) return 7492;
        if (lower.equals("quilt")) return 8271;
        return 0;
    }

    private String inferLoader(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.contains("fabric")) return "Fabric";
        if (lower.contains("neoforge")) return "NeoForge";
        if (lower.contains("forge")) return "Forge";
        if (lower.contains("quilt")) return "Quilt";
        return "Fabric";
    }

    private String extractVersionNumber(String fileName) {
        int idx = fileName.lastIndexOf("-");
        if (idx > 0) {
            String versionPart = fileName.substring(idx + 1);
            if (versionPart.contains(".")) {
                int dotIdx = versionPart.indexOf(".");
                if (dotIdx > 0) {
                    return versionPart.substring(0, dotIdx + 3);
                }
            }
            return versionPart.replace(".jar", "");
        }
        return fileName.replace(".jar", "");
    }

    private String simplifyVersion(String version) {
        if (version == null || version.isEmpty()) return version;

        String cleaned = version.replaceAll("-snapshot.*", "")
                .replaceAll("-pre.*", "")
                .replaceAll("-rc.*", "")
                .replaceAll("-alpha.*", "")
                .replaceAll("-beta.*", "")
                .replaceAll("-dev.*", "")
                .replaceAll("\\+.*", "");

        String[] parts = cleaned.split("\\.");
        if (parts.length >= 2) {
            int major = Integer.parseInt(parts[0]);
            if (major >= 20) {
                return parts[0] + "." + parts[1];
            } else {
                if (parts.length >= 3) {
                    return parts[0] + "." + parts[1] + "." + parts[2];
                }
                return parts[0] + "." + parts[1];
            }
        }
        return cleaned;
    }

    private String getMainVersion(String version) {
        if (version == null || version.isEmpty()) return version;
        String[] parts = version.split("\\.");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[1];
        }
        return version;
    }

    private String formatLoaderName(String loader) {
        if (loader == null || loader.isEmpty()) return loader;
        String lower = loader.toLowerCase();
        if (lower.equals("neoforge")) return "NeoForge";
        if (lower.equals("fabric")) return "Fabric";
        if (lower.equals("forge")) return "Forge";
        if (lower.equals("quilt")) return "Quilt";
        return capitalize(loader);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private boolean isSnapshotVersion(String version) {
        if (version == null || version.isEmpty()) return true;
        String lower = version.toLowerCase();
        return lower.matches("\\d{2}w\\d{2}[a-z]?") ||
                lower.matches("\\d{2}w\\d{2}[a-z]?_\\w+") ||
                lower.contains("pre-") ||
                lower.contains("-pre") ||
                lower.contains("-rc") ||
                lower.contains("snapshot") ||
                lower.contains("oneblock") ||
                lower.contains("potato") ||
                lower.contains("infinite") ||
                lower.contains("craftmine");
    }

    private String formatDownloads(long downloads) {
        if (downloads >= 100_000_000) {
            return String.format("%.2f亿", downloads / 100_000_000.0);
        } else if (downloads >= 10_000) {
            return String.format("%.1f万", downloads / 10_000.0);
        } else {
            return downloads + "次";
        }
    }

    private String formatRelativeTime(String isoTime) {
        if (isoTime == null || isoTime.isEmpty()) return "未知";
        try {
            java.time.Instant instant = java.time.Instant.parse(isoTime);
            java.time.Duration diff = java.time.Duration.between(
                    instant, java.time.Instant.now());

            long minutes = diff.toMinutes();
            if (minutes < 1) return "刚刚";
            if (minutes < 60) return minutes + "分钟前";

            long hours = diff.toHours();
            if (hours < 24) return hours + "小时前";

            long days = diff.toDays();
            if (days < 30) return days + "天前";

            long months = days / 30;
            if (months < 12) return months + "个月前";

            long years = days / 365;
            return years + "年前";
        } catch (Exception e) {
            return isoTime;
        }
    }
}