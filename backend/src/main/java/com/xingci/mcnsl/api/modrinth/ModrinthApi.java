package com.xingci.mcnsl.api.modrinth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingci.mcnsl.util.HttpUtils;

/**
 * Modrinth API 封装
 * <p>
 * 文档: https://docs.modrinth.com/api
 */
@Component
public class ModrinthApi {

    private static final String BASE = "https://api.modrinth.com/v2";

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 搜索 Modrinth 上的模组
     *
     * @param query   搜索关键词（可为空，空则返回热门模组）
     * @param offset  偏移量
     * @param limit   每页数量（最大 100）
     * @param loader  加载器过滤（fabric / forge / neoforge / quilt，可为 null）
     * @param version MC 版本过滤（如 1.21.1，可为 null）
     * @return 模组列表
     */
    public List<Map<String, Object>> searchMods(
            String query,
            int offset,
            int limit,
            String loader,
            String version
    ) throws Exception {

        StringBuilder url = new StringBuilder(BASE);
        url.append("/search?limit=").append(Math.min(limit, 100));
        url.append("&offset=").append(offset);

        // 只搜索 mod 类型
        List<String> facets = new ArrayList<>();
        facets.add("[\"project_type:mod\"]");

        if (loader != null && !loader.isBlank()) {
            facets.add("[\"categories:" + loader + "\"]");
        }
        if (version != null && !version.isBlank()) {
            facets.add("[\"versions:" + version + "\"]");
        }

        url.append("&facets=").append(URLEncoder.encode(
                facets.toString(), StandardCharsets.UTF_8));

        if (query != null && !query.isBlank()) {
            url.append("&query=").append(URLEncoder.encode(
                    query, StandardCharsets.UTF_8));
        }

        String json = HttpUtils.get(url.toString());
        JsonNode root = mapper.readTree(json);

        List<Map<String, Object>> result = new ArrayList<>();

        for (JsonNode hit : root.path("hits")) {
            Map<String, Object> mod = new LinkedHashMap<>();
            mod.put("id", hit.path("project_id").asText());
            mod.put("slug", hit.path("slug").asText());
            mod.put("name", hit.path("title").asText());
            mod.put("description", hit.path("description").asText());
            mod.put("icon", hit.path("icon_url").asText());
            mod.put("downloads", formatDownloads(hit.path("downloads").asLong()));
            mod.put("follows", hit.path("follows").asInt());
            mod.put("updatedAt", formatRelativeTime(hit.path("date_modified").asText()));
            mod.put("source", "modrinth");

            // categories 包含加载器
            List<String> categories = new ArrayList<>();
            for (JsonNode cat : hit.path("categories")) {
                categories.add(cat.asText());
            }
            mod.put("categories", categories);

            // 提取加载器
            List<String> loaders = extractLoaders(categories);
            mod.put("loaders", loaders);

            // 游戏版本
            List<String> versions = new ArrayList<>();
            for (JsonNode v : hit.path("versions")) {
                versions.add(v.asText());
            }
            mod.put("versions", versions);
            mod.put("versionRange", formatVersionRange(versions));

            // 作者
            mod.put("author", hit.path("author").asText());

            // license
            mod.put("license", hit.path("license").path("id").asText());

            result.add(mod);
        }

        return result;
    }

    /**
     * 获取模组的所有版本
     *
     * @param slug 模组 slug 或 ID
     * @return 版本列表（按加载器和 MC 版本分组）
     */
    public List<Map<String, Object>> getModVersions(String slug) throws Exception {

        String url = BASE + "/project/" + slug + "/version";
        String json = HttpUtils.get(url);

        JsonNode root = mapper.readTree(json);

        // 按 loader + mcVersion 分组
        Map<String, Map<String, Object>> groups = new LinkedHashMap<>();

        for (JsonNode v : root) {
            String versionName = v.path("name").asText();
            String versionNumber = v.path("version_number").asText();
            String versionType = v.path("version_type").asText();
            String datePublished = v.path("date_published").asText();
            long downloads = v.path("downloads").asLong();
            boolean featured = v.path("featured").asBoolean();

            // 文件信息
            String fileName = "";
            String fileUrl = "";
            for (JsonNode f : v.path("files")) {
                if (f.path("primary").asBoolean()) {
                    fileName = f.path("filename").asText();
                    fileUrl = f.path("url").asText();
                    break;
                }
                if (fileName.isEmpty()) {
                    fileName = f.path("filename").asText();
                    fileUrl = f.path("url").asText();
                }
            }

            // 加载器列表
            List<String> loaders = new ArrayList<>();
            for (JsonNode l : v.path("loaders")) {
                loaders.add(l.asText());
            }

            // 游戏版本列表
            List<String> gameVersions = new ArrayList<>();
            for (JsonNode gv : v.path("game_versions")) {
                gameVersions.add(gv.asText());
            }

            // 为每个 loader + mcVersion 组合创建分组
            for (String loader : loaders) {
                String loaderDisplay = formatLoaderName(loader);
                for (String gv : gameVersions) {
                    String mcVersion = simplifyVersion(gv);
                    String groupKey = loader + "|" + mcVersion;

                    if (!groups.containsKey(groupKey)) {
                        Map<String, Object> group = new LinkedHashMap<>();
                        group.put("title", loaderDisplay + " " + mcVersion);
                        group.put("loader", loader);
                        group.put("mcVersion", mcVersion);
                        group.put("variants", new ArrayList<Map<String, Object>>());
                        groups.put(groupKey, group);
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> variants =
                            (List<Map<String, Object>>) groups.get(groupKey).get("variants");

                    Map<String, Object> variant = new LinkedHashMap<>();
                    variant.put("name", versionName);
                    variant.put("versionNumber", versionNumber);
                    variant.put("loader", loaderDisplay);
                    variant.put("fileName", fileName);
                    variant.put("fileUrl", fileUrl);
                    variant.put("downloads", formatDownloads(downloads));
                    variant.put("updatedAt", formatRelativeTime(datePublished));
                    variant.put("isBeta", "beta".equals(versionType) || "alpha".equals(versionType));
                    variant.put("versionType", versionType);
                    variant.put("featured", featured);

                    // 图标用模组自身的图标
                    variant.put("icon", BASE + "/project/" + slug + "/icon");

                    variants.add(variant);
                }
            }
        }

        return new ArrayList<>(groups.values());
    }

    /**
     * 获取单个模组的详细信息
     */
    public Map<String, Object> getProject(String slug) throws Exception {
        String url = BASE + "/project/" + slug;
        String json = HttpUtils.get(url);
        JsonNode root = mapper.readTree(json);

        Map<String, Object> mod = new LinkedHashMap<>();
        mod.put("id", root.path("id").asText());
        mod.put("slug", root.path("slug").asText());
        mod.put("name", root.path("title").asText());
        mod.put("description", root.path("description").asText());
        mod.put("icon", root.path("icon_url").asText());
        mod.put("downloads", formatDownloads(root.path("downloads").asLong()));
        mod.put("follows", root.path("followers").asInt());
        mod.put("updatedAt", formatRelativeTime(root.path("date_modified").asText(root.path("updated").asText())));
        mod.put("source", "modrinth");
        mod.put("body", root.path("body").asText());
        mod.put("license", root.path("license").path("id").asText());

        List<String> categories = new ArrayList<>();
        for (JsonNode cat : root.path("categories")) {
            categories.add(cat.asText());
        }
        mod.put("categories", categories);
        mod.put("loaders", extractLoaders(categories));

        List<String> versions = new ArrayList<>();
        for (JsonNode v : root.path("versions")) {
            versions.add(v.asText());
        }
        mod.put("versions", versions);
        mod.put("versionRange", formatVersionRange(versions));

        return mod;
    }

    // ========== 工具方法 ==========

    private List<String> extractLoaders(List<String> categories) {
        List<String> loaders = new ArrayList<>();
        for (String cat : categories) {
            String lower = cat.toLowerCase();
            if (lower.equals("fabric") || lower.equals("forge")
                    || lower.equals("neoforge") || lower.equals("quilt")
                    || lower.equals("rift") || lower.equals("liteloader")) {
                if (lower.equals("neoforge")) {
                    loaders.add("NeoForge");
                } else {
                    loaders.add(capitalize(cat));
                }
            }
        }
        return loaders;
    }

    private String formatVersionRange(List<String> versions) {
        if (versions.isEmpty()) return "";
        if (versions.size() <= 3) return String.join(", ", versions);
        return versions.get(0) + " ~ " + versions.get(versions.size() - 1)
                + " (共" + versions.size() + "个版本)";
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

    private String simplifyVersion(String version) {
        // "1.21.1" -> "1.21.1", "1.21" -> "1.21"
        // 取前两段作为主版本号
        String[] parts = version.split("\\.");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[1];
        }
        return version;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
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
}
