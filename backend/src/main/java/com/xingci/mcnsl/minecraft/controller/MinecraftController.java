package com.xingci.mcnsl.minecraft.controller;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xingci.mcnsl.minecraft.install.MinecraftInstallManager;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstance;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstanceManager;
import com.xingci.mcnsl.minecraft.launch.LaunchOptions;
import com.xingci.mcnsl.minecraft.launch.MinecraftLauncher;
import com.xingci.mcnsl.minecraft.launch.ProcessManager;
import com.xingci.mcnsl.minecraft.util.JavaUtils;



/**
 * Minecraft 总控制器
 *
 * 前端统一入口
 */
@RestController
@RequestMapping("/api/minecraft")
@CrossOrigin
public class MinecraftController {



    private final MinecraftInstanceManager instanceManager;


    private final MinecraftInstallManager installManager;


    private final MinecraftLauncher launcher;


    private final ProcessManager processManager;





    public MinecraftController(
            MinecraftInstanceManager instanceManager,
            MinecraftInstallManager installManager,
            MinecraftLauncher launcher,
            ProcessManager processManager
    ){

        this.instanceManager =
                instanceManager;


        this.installManager =
                installManager;


        this.launcher =
                launcher;


        this.processManager =
                processManager;

    }









    /**
     * 创建 Minecraft 实例
     *
     * POST
     *
     * {
     *   "name":"生存服",
     *   "version":"1.21.8",
     *   "loader":"fabric"
     * }
     */
    @PostMapping("/instance")
    public MinecraftInstance create(
            @RequestBody Map<String,String> body
    )
            throws Exception {



        return instanceManager.create(

                body.get("name"),

                body.get("version"),

                body.get("loader")

        );

    }









    /**
     * 安装实例
     */
    @PostMapping("/instance/{id}/install")
    public Map<String,String> install(
            @PathVariable String id
    )
            throws Exception {



        installManager.installInstance(
                id
        );



        return Map.of(

                "status",
                "success"

        );

    }









    /**
     * 启动实例
     */
    @PostMapping("/instance/{id}/start")
    public Map<String,String> start(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body
    )
            throws Exception {



        MinecraftInstance instance =
                instanceManager.get(id);


        if(instance == null){
            Path instancesDir = Path.of(System.getProperty("user.dir")).resolve("instances");
            java.io.File[] instanceDirs = instancesDir.toFile().listFiles(java.io.File::isDirectory);
            
            if (instanceDirs != null) {
                for (java.io.File instanceDir : instanceDirs) {
                    if (instanceDir.getName().equals(id)) {
                        instance = new MinecraftInstance();
                        instance.setId(id);
                        instance.setName(id);
                        instance.setPath(instanceDir.getAbsolutePath());
                        
                        Path versionsDir = instanceDir.toPath().resolve("versions");
                        java.io.File[] versionDirs = versionsDir.toFile().listFiles(java.io.File::isDirectory);
                        if (versionDirs != null && versionDirs.length > 0) {
                            instance.setVersion(versionDirs[0].getName());
                        }
                        break;
                    }
                }
            }
        }


        if(instance == null){


            throw new RuntimeException(
                    "实例不存在"
            );


        }




        String processId =
                UUID.randomUUID()
                        .toString();




        String username = body != null ? body.get("username") : null;
        String uuid = body != null ? body.get("uuid") : null;
        String accessToken = body != null ? body.get("accessToken") : null;
        String userType = body != null ? body.get("userType") : null;

        if (username == null || username.isEmpty()) {
            username = "Player";
        }

        JavaUtils.JavaInfo javaInfo = JavaUtils.findJavaForMinecraftVersion(instance.getVersion());
        Path javaPath = null;
        if (javaInfo != null) {
            javaPath = javaInfo.getJavaExecutable();
            System.out.println("选择Java版本: " + javaInfo.getVersion() + " (" + javaPath + ")");
        } else if (instance.getJavaPath() != null && !instance.getJavaPath().isEmpty()) {
            javaPath = Path.of(instance.getJavaPath());
            System.out.println("使用实例配置的Java路径: " + javaPath);
        }

        LaunchOptions options =
                LaunchOptions.builder()

                        .version(
                                instance.getVersion()
                        )


                        .gameDir(

                                Path.of(
                                        instance.getPath()
                                )

                        )


                        .javaPath(javaPath)


                        .minMemory(
                                instance.getMinMemory()
                        )


                        .maxMemory(
                                instance.getMaxMemory()
                        )


                        .username(username)
                        .uuid(uuid)
                        .accessToken(accessToken)
                        .userType(userType)


                        .build();




        launcher.launch(

                processId,

                options,


                // 修复 println 方法引用歧义
                message -> {

                    System.out.println(
                            message
                    );

                }


        );




        return Map.of(

                "processId",

                processId,


                "status",

                "started"

        );


    }









    /**
     * 停止实例
     */
    @PostMapping("/process/{id}/stop")
    public boolean stop(
            @PathVariable String id
    ){



        return processManager.stop(
                id
        );


    }









    /**
     * 查询运行状态
     */
    @GetMapping("/process/{id}")
    public Map<String,Object> status(
            @PathVariable String id
    ){



        return Map.of(

                "running",

                processManager.isRunning(id)

        );


    }









    /**
     * 删除实例
     */
    @DeleteMapping("/instance/{id}")
    public boolean delete(
            @PathVariable String id
    )
            throws Exception {



        return instanceManager.delete(
                id
        );


    }


    /**
     * 检测实例版本是否已安装
     */
    @GetMapping("/instance/{id}/installed")
    public Map<String, Object> checkInstalled(
            @PathVariable String id
    )
            throws Exception {

        MinecraftInstance instance =
                instanceManager.get(id);

        if(instance == null){
            throw new RuntimeException("实例不存在");
        }

        Path gameDir = Path.of(instance.getPath());
        Path versionDir = gameDir.resolve("versions").resolve(instance.getVersion());
        Path versionJson = versionDir.resolve(instance.getVersion() + ".json");
        Path versionJar = versionDir.resolve(instance.getVersion() + ".jar");

        boolean jsonExists = versionJson.toFile().exists();
        boolean jarExists = versionJar.toFile().exists();
        boolean assetsExists = gameDir.resolve("assets").toFile().exists();

        return Map.of(
                "instanceId", id,
                "version", instance.getVersion(),
                "installed", jsonExists && jarExists && assetsExists,
                "jsonExists", jsonExists,
                "jarExists", jarExists,
                "assetsExists", assetsExists,
                "gameDir", gameDir.toString()
        );
    }


    /**
     * 获取已安装的版本列表
     */
    @GetMapping("/versions/installed")
    public List<Map<String, Object>> getInstalledVersions(
            @RequestParam(required = false) String instanceId
    )
            throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, MinecraftInstance> pathToInstance = new java.util.HashMap<>();
        for (MinecraftInstance instance : instanceManager.list()) {
            pathToInstance.put(instance.getPath(), instance);
        }

        Path instancesDir = Path.of(System.getProperty("user.dir")).resolve("instances");
        if (!instancesDir.toFile().exists()) {
            return List.of();
        }

        java.io.File[] instanceDirs = instancesDir.toFile().listFiles(java.io.File::isDirectory);
        if (instanceDirs == null) {
            return List.of();
        }

        for (java.io.File instanceDir : instanceDirs) {
            String instancePath = instanceDir.getAbsolutePath();
            MinecraftInstance instance = pathToInstance.get(instancePath);
            
            String instanceUuid = instance != null ? instance.getId() : instanceDir.getName();
            String instanceName = instance != null ? instance.getName() : instanceDir.getName();

            Path versionsDir = instanceDir.toPath().resolve("versions");
            if (!versionsDir.toFile().exists()) {
                continue;
            }

            java.io.File[] versionDirs = versionsDir.toFile().listFiles(java.io.File::isDirectory);
            if (versionDirs == null) {
                continue;
            }

            for (java.io.File versionDir : versionDirs) {
                String versionName = versionDir.getName();
                Path versionJson = versionDir.toPath().resolve(versionName + ".json");
                Path versionJar = versionDir.toPath().resolve(versionName + ".jar");

                if (versionJson.toFile().exists() && versionJar.toFile().exists()) {
                    result.add(Map.of(
                            "instanceId", instanceUuid,
                            "instanceName", instanceName,
                            "version", versionName,
                            "path", versionDir.getAbsolutePath(),
                            "gameDir", instancePath
                    ));
                }
            }
        }

        return result;
    }


    /**
     * 生成离线UUID（用于前端展示）
     */
    @GetMapping("/uuid/offline")
    public Map<String, String> generateOfflineUUID(
            @RequestParam String username
    ) {
        return Map.of(
                "username", username,
                "uuid", com.xingci.mcnsl.minecraft.util.UUIDUtils.generateOfflineUUIDString(username)
        );
    }


}