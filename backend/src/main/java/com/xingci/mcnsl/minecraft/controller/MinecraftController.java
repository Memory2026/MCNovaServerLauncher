package com.xingci.mcnsl.minecraft.controller;


import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.xingci.mcnsl.minecraft.websocket.MinecraftLogWebSocket;



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


    private final MinecraftLogWebSocket minecraftLogWebSocket;


    private static java.util.List<Map<String, Object>> logBuffer = new java.util.concurrent.CopyOnWriteArrayList<>();
    private static int logIdCounter = 0;





    public MinecraftController(
            MinecraftInstanceManager instanceManager,
            MinecraftInstallManager installManager,
            MinecraftLauncher launcher,
            ProcessManager processManager,
            MinecraftLogWebSocket minecraftLogWebSocket
    ){

        this.instanceManager =
                instanceManager;


        this.installManager =
                installManager;


        this.launcher =
                launcher;


        this.processManager =
                processManager;


        this.minecraftLogWebSocket =
                minecraftLogWebSocket;

    }

    public static void addLog(String message) {
        logBuffer.add(Map.of(
                "id", ++logIdCounter,
                "message", message
        ));
        if (logBuffer.size() > 1000) {
            logBuffer = logBuffer.subList(logBuffer.size() - 500, logBuffer.size());
        }
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
            @RequestBody(required = false) Map<String, Object> body
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




        String username = body != null ? String.valueOf(body.get("username")) : null;
        String uuid = body != null ? String.valueOf(body.get("uuid")) : null;
        String accessToken = body != null ? String.valueOf(body.get("accessToken")) : null;
        String userType = body != null ? String.valueOf(body.get("userType")) : null;
        Integer minMemory = null;
        Integer maxMemory = null;
        
        if (body != null) {
            Object minMemObj = body.get("minMemory");
            if (minMemObj != null) {
                minMemory = minMemObj instanceof Number ? ((Number) minMemObj).intValue() : Integer.parseInt(String.valueOf(minMemObj));
            }
            Object maxMemObj = body.get("maxMemory");
            if (maxMemObj != null) {
                maxMemory = maxMemObj instanceof Number ? ((Number) maxMemObj).intValue() : Integer.parseInt(String.valueOf(maxMemObj));
            }
        }

        if (username == null || username.isEmpty() || username.equals("null")) {
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
                        .version(instance.getVersion())
                        .gameDir(Path.of(instance.getPath()))
                        .javaPath(javaPath)
                        .minMemory(minMemory != null ? minMemory : instance.getMinMemory())
                        .maxMemory(maxMemory != null ? maxMemory : instance.getMaxMemory())
                        .username(username)
                        .uuid(uuid)
                        .accessToken(accessToken)
                        .userType(userType)
                        .build();




        launcher.launch(

                processId,

                options,


                message -> {

                    System.out.println(
                            message
                    );

                    addLog(message);
                    minecraftLogWebSocket.send(processId, message);

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

    @GetMapping("/logs/latest")
    public Map<String, Object> getLatestLogs(@RequestParam(defaultValue = "0") int lastId) {
        java.util.List<Map<String, Object>> newLogs = logBuffer.stream()
                .filter(log -> (int) log.get("id") > lastId)
                .toList();
        return Map.of("logs", newLogs);
    }

    @PutMapping("/instance/{id}/settings")
    public Map<String, Object> updateSettings(
            @PathVariable String id,
            @RequestBody Map<String, Object> body
    ) {
        MinecraftInstance instance = instanceManager.get(id);
        
        if (instance == null) {
            return Map.of("success", false, "message", "实例不存在");
        }

        if (body.containsKey("javaPath")) {
            String javaPath = String.valueOf(body.get("javaPath"));
            if (!javaPath.equals("null") && !javaPath.isEmpty()) {
                instance.setJavaPath(javaPath);
            }
        }

        if (body.containsKey("minMemory")) {
            Object minMemObj = body.get("minMemory");
            int minMemory = minMemObj instanceof Number ? ((Number) minMemObj).intValue() : Integer.parseInt(String.valueOf(minMemObj));
            instance.setMinMemory(minMemory);
        }

        if (body.containsKey("maxMemory")) {
            Object maxMemObj = body.get("maxMemory");
            int maxMemory = maxMemObj instanceof Number ? ((Number) maxMemObj).intValue() : Integer.parseInt(String.valueOf(maxMemObj));
            instance.setMaxMemory(maxMemory);
        }

        if (body.containsKey("jvmParameters")) {
            String jvmParams = String.valueOf(body.get("jvmParameters"));
            if (!jvmParams.equals("null")) {
                instance.setJvmParameters(jvmParams);
            }
        }

        if (body.containsKey("username")) {
            String username = String.valueOf(body.get("username"));
            if (!username.equals("null") && !username.isEmpty()) {
                instance.setUsername(username);
            }
        }

        instanceManager.save(instance);
        
        return Map.of("success", true, "message", "设置已保存");
    }

    @GetMapping("/logs/folder")
    public Map<String, Object> openLogFolder() {
        try {
            Path logDir = Paths.get(System.getProperty("user.dir"), "backend", "logs");
            if (!logDir.toFile().exists()) {
                logDir.toFile().mkdirs();
            }
            
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + logDir.toAbsolutePath());
            } else if (os.contains("win")) {
                Runtime.getRuntime().exec("explorer " + logDir.toAbsolutePath());
            } else {
                Runtime.getRuntime().exec("xdg-open " + logDir.toAbsolutePath());
            }
            
            return Map.of("success", true, "path", logDir.toAbsolutePath().toString());
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }


}