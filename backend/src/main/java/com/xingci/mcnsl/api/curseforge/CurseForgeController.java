package com.xingci.mcnsl.api.curseforge;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/curseforge")
@CrossOrigin
public class CurseForgeController {

    private final CurseForgeApi curseForgeApi;

    public CurseForgeController(CurseForgeApi curseForgeApi) {
        this.curseForgeApi = curseForgeApi;
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String loader,
            @RequestParam(required = false) String version
    ) throws Exception {
        return curseForgeApi.searchMods(query, offset, limit, loader, version);
    }

    @GetMapping("/project/{projectId}")
    public Map<String, Object> getProject(@PathVariable String projectId) throws Exception {
        return curseForgeApi.getProject(projectId);
    }

    @GetMapping("/project/{projectId}/versions")
    public List<Map<String, Object>> getVersions(@PathVariable String projectId) throws Exception {
        return curseForgeApi.getModVersions(projectId);
    }
}