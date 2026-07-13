package com.xingci.mcnsl.minecraft.install;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.xingci.mcnsl.minecraft.download.InstallStatus;
import com.xingci.mcnsl.minecraft.download.MinecraftDownloader;
import com.xingci.mcnsl.minecraft.download.MinecraftInstallTask;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstance;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstanceManager;
import com.xingci.mcnsl.minecraft.loader.FabricInstaller;
import com.xingci.mcnsl.minecraft.loader.ForgeInstaller;
import com.xingci.mcnsl.minecraft.loader.NeoForgeInstaller;
import com.xingci.mcnsl.minecraft.loader.OptiFineInstaller;
import com.xingci.mcnsl.websocket.MinecraftProgressSocket;


@Component
public class MinecraftInstallManager {


    private final MinecraftDownloader downloader;

    private final MinecraftInstanceManager instanceManager;

    private final MinecraftProgressSocket progressSocket;

    private final FabricInstaller fabricInstaller;

    private final ForgeInstaller forgeInstaller;

    private final NeoForgeInstaller neoForgeInstaller;

    private final OptiFineInstaller optiFineInstaller;


    private final Map<String, MinecraftInstallTask> tasks =
            new ConcurrentHashMap<>();


    public MinecraftInstallManager(
            MinecraftDownloader downloader,
            MinecraftInstanceManager instanceManager,
            MinecraftProgressSocket progressSocket,
            FabricInstaller fabricInstaller,
            ForgeInstaller forgeInstaller,
            NeoForgeInstaller neoForgeInstaller,
            OptiFineInstaller optiFineInstaller
    ){

        this.downloader = downloader;
        this.instanceManager = instanceManager;
        this.progressSocket = progressSocket;
        this.fabricInstaller = fabricInstaller;
        this.forgeInstaller = forgeInstaller;
        this.neoForgeInstaller = neoForgeInstaller;
        this.optiFineInstaller = optiFineInstaller;

    }


    public MinecraftInstallTask installInstance(
            String id
    )
            throws Exception {

        return installInstance(id, null, null);

    }


    public MinecraftInstallTask installInstance(
            String id,
            String loaderVersion
    )
            throws Exception {

        return installInstance(id, loaderVersion, null);

    }


    public MinecraftInstallTask installInstance(
            String id,
            String loaderVersion,
            String fabricApiVersion
    )
            throws Exception {

        MinecraftInstance instance =
                instanceManager.get(id);

        if(instance == null){
            throw new RuntimeException("实例不存在");
        }

        MinecraftInstallTask task =
                new MinecraftInstallTask();

        task.setVersion(instance.getVersion());
        task.setListener(() -> progressSocket.send(task));

        tasks.put(task.getId(), task);

        Path gameDir = Path.of(instance.getPath());

        Thread thread = new Thread(() -> {
            try {
                downloader.download(instance.getVersion(), gameDir, task);

                installLoaderIfNeeded(instance, loaderVersion, fabricApiVersion, gameDir, task);

                instance.setStatus("INSTALLED");
                instanceManager.save(instance);

                task.success();
            }
            catch(Exception e){
                task.failed(e.getMessage());
            }
        });

        thread.setName("Minecraft-Install-" + task.getId());
        thread.start();

        return task;

    }


    private void installLoaderIfNeeded(
            MinecraftInstance instance,
            String loaderVersion,
            String fabricApiVersion,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {

        String loader = instance.getLoader();

        if(loader == null || loader.isBlank() || "vanilla".equalsIgnoreCase(loader)) {
            return;
        }

        if(loaderVersion == null || loaderVersion.isBlank() || loaderVersion.startsWith("暂无")) {
            throw new RuntimeException("未选择可用的 " + loader + " 版本");
        }

        String minecraftVersion = instance.getVersion();

        if("fabric".equalsIgnoreCase(loader)) {
            task.setStep("安装 Fabric Loader");
            task.setCurrentFile(loaderVersion);

            fabricInstaller.install(minecraftVersion, loaderVersion, gameDir);

            String newVersion = "fabric-loader-" + loaderVersion + "-" + minecraftVersion;
            instance.setVersion(newVersion);

            if(fabricApiVersion != null && !fabricApiVersion.isBlank() && !"none".equalsIgnoreCase(fabricApiVersion)) {
                installFabricApi(fabricApiVersion, minecraftVersion, gameDir, task);
            }

            return;
        }

        if("forge".equalsIgnoreCase(loader)) {
            task.setStep("安装 Forge Loader");
            task.setCurrentFile(loaderVersion);

            forgeInstaller.install(minecraftVersion, loaderVersion, gameDir);

            instance.setVersion("forge-" + loaderVersion);
            return;
        }

        if("neoforge".equalsIgnoreCase(loader)) {
            task.setStep("安装 NeoForge Loader");
            task.setCurrentFile(loaderVersion);

            neoForgeInstaller.install(minecraftVersion, loaderVersion, gameDir);

            instance.setVersion("neoforge-" + loaderVersion);
            return;
        }

        if("optifine".equalsIgnoreCase(loader)) {
            task.setStep("安装 OptiFine");
            task.setCurrentFile(loaderVersion);

            optiFineInstaller.install(minecraftVersion, loaderVersion, gameDir);

            instance.setVersion("optifine-" + loaderVersion);
            return;
        }

        throw new RuntimeException(loader + " 安装器暂未接入");

    }


    private void installFabricApi(
            String fabricApiVersion,
            String minecraftVersion,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {

        task.setStep("安装 Fabric API");
        task.setCurrentFile("fabric-api-" + fabricApiVersion);

        Path modsDir = gameDir.resolve("mods");
        Files.createDirectories(modsDir);

        String mavenVersion = fabricApiVersion.replace("+", "-");
        String url = "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/" + mavenVersion + "/fabric-api-" + mavenVersion + ".jar";

        Path target = modsDir.resolve("fabric-api-" + fabricApiVersion + ".jar");

        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        java.net.http.HttpResponse<Path> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofFile(target));

        if(response.statusCode() != 200){
            throw new java.io.IOException("Fabric API 下载失败: " + response.statusCode());
        }

    }


    public MinecraftInstallTask getTask(String id){
        return tasks.get(id);
    }


    public Map<String, MinecraftInstallTask> getTasks(){
        return tasks;
    }


    public void clearFinished(){
        tasks.entrySet().removeIf(entry -> {
            MinecraftInstallTask task = entry.getValue();
            return task.getStatus() == InstallStatus.SUCCESS || task.getStatus() == InstallStatus.FAILED;
        });
    }


}