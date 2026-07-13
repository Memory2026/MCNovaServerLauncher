package com.xingci.mcnsl.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xingci.mcnsl.manager.loader.LoaderManager;
import com.xingci.mcnsl.minecraft.download.MinecraftInstallTask;
import com.xingci.mcnsl.minecraft.install.MinecraftInstallManager;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstance;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstanceManager;
import com.xingci.mcnsl.model.LoaderVersionInfo;
import com.xingci.mcnsl.service.LoaderVersionService;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/download")
@CrossOrigin(origins = "*")
public class DownloadController {

    private final LoaderVersionService loaderVersionService;
    private final LoaderManager loaderManager;
    private final MinecraftInstanceManager instanceManager;
    private final MinecraftInstallManager installManager;

    public DownloadController(
            LoaderVersionService loaderVersionService,
            LoaderManager loaderManager,
            MinecraftInstanceManager instanceManager,
            MinecraftInstallManager installManager
    ) {
        this.loaderVersionService = loaderVersionService;
        this.loaderManager = loaderManager;
        this.instanceManager = instanceManager;
        this.installManager = installManager;
    }

    @Getter
    @Setter
    public static class InstallRequest {

        private String versionId;

        private String type;

        private String loaderType;

        private String loaderVersion;

        private String fabricApiVersion;

        private String customName;
    }

    @PostMapping("/install")
    public ResponseEntity<?> install(
            @RequestBody InstallRequest request
    )
            throws Exception {

        String versionId =
                request.getVersionId();

        if(versionId == null
                ||
                versionId.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "code",
                                    400,
                                    "message",
                                    "缺少 Minecraft 版本"
                            )
                    );

        }

        String loaderType =
                request.getLoaderType() == null
                        ||
                        request.getLoaderType().isBlank()
                        ?
                        "vanilla"
                        :
                        request.getLoaderType();

        if(!"vanilla".equalsIgnoreCase(loaderType)
                &&
                !isLoaderVersionUsable(request.getLoaderVersion())) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "code",
                                    400,
                                    "message",
                                    "缺少可用的加载器版本"
                            )
                    );

        }

        String instanceName = request.getCustomName();
        if (instanceName == null || instanceName.isBlank()) {
            instanceName = versionId + " " + loaderType;
        }

        for (MinecraftInstance existing : instanceManager.list()) {
            if (existing.getName().equals(instanceName)) {
                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "code",
                                        400,
                                        "message",
                                        "版本名称已存在，请使用其他名称"
                                )
                        );
            }
        }

        MinecraftInstance instance =
                instanceManager.create(
                        instanceName,
                        versionId,
                        loaderType,
                        defaultGameDir(instanceName)
                );

        MinecraftInstallTask task =
                installManager.installInstance(
                        instance.getId(),
                        request.getLoaderVersion(),
                        request.getFabricApiVersion()
                );

        return ResponseEntity.ok(
                Map.of(
                        "code",
                        0,
                        "message",
                        "安装任务已创建",
                        "instanceId",
                        instance.getId(),
                        "taskId",
                        task.getId()
                )
        );
    }

    @GetMapping("/{loader}/versions")
    public List<String> getLoaderVersions(
            @PathVariable String loader,
            @RequestParam String mcVersion
    ) {

        List<String> detected =
                loaderManager.getVersions(loader, mcVersion)
                        .stream()
                        .map(LoaderVersionInfo::getVersion)
                        .filter(this::isLoaderVersionUsable)
                        .distinct()
                        .toList();

        if(!detected.isEmpty()) {

            return detected;

        }

        return loaderVersionService.getLoaderVersions(
                loader,
                mcVersion
        );
    }

    @GetMapping("/fabric/versions/detail")
    public List<LoaderVersionInfo> getFabricVersionsDetail(
            @RequestParam String mcVersion
    ) {

        List<LoaderVersionInfo> versions =
                loaderManager.getVersions("fabric", mcVersion);

        if(!versions.isEmpty()) {

            return versions;

        }

        return List.of();
    }

    @GetMapping("/fabric-api/versions")
    public List<String> getFabricApiVersions(
            @RequestParam String mcVersion
    ) {

        return loaderVersionService.getFabricApiVersions(
                mcVersion
        );
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getDownloadTasks() {
        return ResponseEntity.ok(installManager.getTasks().values());
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<?> getDownloadTask(@PathVariable String taskId) {
        MinecraftInstallTask task = installManager.getTask(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    private boolean isLoaderVersionUsable(
            String version
    ) {

        return version != null
                &&
                !version.isBlank()
                &&
                !version.startsWith("暂无");

    }

    private Path defaultGameDir(
            String instanceName
    ) {

        String safeName =
                instanceName.replaceAll("[^a-zA-Z0-9._-]", "_");

        Path projectDir = Path.of(System.getProperty("user.dir"));
        Path instancesDir = projectDir.resolve("instances");

        return instancesDir.resolve(safeName);

    }

}
