package com.xingci.mcnsl.minecraft.controller;


import com.xingci.mcnsl.minecraft.instance.MinecraftInstance;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstanceManager;

import com.xingci.mcnsl.minecraft.install.MinecraftInstallManager;

import com.xingci.mcnsl.minecraft.launch.LaunchOptions;
import com.xingci.mcnsl.minecraft.launch.MinecraftLauncher;
import com.xingci.mcnsl.minecraft.launch.ProcessManager;


import org.springframework.web.bind.annotation.*;


import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;



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
            @PathVariable String id
    )
            throws Exception {



        MinecraftInstance instance =
                instanceManager.get(id);




        if(instance == null){


            throw new RuntimeException(
                    "实例不存在"
            );


        }






        String processId =
                UUID.randomUUID()
                        .toString();








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


                        .javaPath(

                                instance.getJavaPath() == null

                                        ?

                                        null

                                        :

                                        Path.of(
                                                instance.getJavaPath()
                                        )

                        )


                        .minMemory(
                                instance.getMinMemory()
                        )


                        .maxMemory(
                                instance.getMaxMemory()
                        )


                        .username(
                                "Player"
                        )


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


}