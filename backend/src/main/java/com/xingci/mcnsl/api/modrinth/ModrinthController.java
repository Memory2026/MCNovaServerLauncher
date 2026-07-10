package com.xingci.mcnsl.api.modrinth;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Modrinth 模组搜索控制器
 * <p>
 * 前端统一入口: /api/modrinth
 */
@RestController
@RequestMapping("/api/modrinth")
@CrossOrigin
public class ModrinthController {

    private final ModrinthApi modrinthApi;

    public ModrinthController(ModrinthApi modrinthApi) {
        this.modrinthApi = modrinthApi;
    }

    /**
     * 搜索 Modrinth 模组
     * <p>
     * GET /api/modrinth/search?query=&offset=0&limit=20&loader=fabric&version=1.21.1
     */
    @GetMapping("/search")
    public List<Map<String, Object>> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String loader,
            @RequestParam(required = false) String version
    ) throws Exception {
        return modrinthApi.searchMods(query, offset, limit, loader, version);
    }

    /**
     * 获取模组详情
     * <p>
     * GET /api/modrinth/project/{slug}
     */
    @GetMapping("/project/{slug}")
    public Map<String, Object> getProject(@PathVariable String slug) throws Exception {
        return modrinthApi.getProject(slug);
    }

    /**
     * 获取模组的所有版本（按加载器和 MC 版本分组）
     * <p>
     * GET /api/modrinth/project/{slug}/versions
     */
    @GetMapping("/project/{slug}/versions")
    public List<Map<String, Object>> getVersions(@PathVariable String slug) throws Exception {
        return modrinthApi.getModVersions(slug);
    }
}
