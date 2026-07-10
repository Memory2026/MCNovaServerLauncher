package com.xingci.mcnsl.minecraft.install;


import com.xingci.mcnsl.minecraft.download.MinecraftInstallTask;


import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.Map;



/**
 * Minecraft安装控制器
 *
 * 管理客户端安装流程
 */
@RestController
@RequestMapping("/api/minecraft/install")
@CrossOrigin
public class MinecraftInstallController {



    private final MinecraftInstallManager installManager;






    public MinecraftInstallController(
            MinecraftInstallManager installManager
    ){

        this.installManager =
                installManager;

    }









    /**
     * 安装实例
     *
     * POST
     *
     * /api/minecraft/install/instance/{id}
     */
    @PostMapping("/instance/{id}")
    public MinecraftInstallTask installInstance(
            @PathVariable String id
    )
            throws Exception {


        return installManager.installInstance(
                id
        );


    }









    /**
     * 获取安装任务
     *
     * GET
     *
     * /api/minecraft/install/task/{id}
     */
    @GetMapping("/task/{id}")
    public MinecraftInstallTask getTask(
            @PathVariable String id
    ){


        return installManager.getTask(
                id
        );


    }









    /**
     * 获取全部安装任务
     *
     * GET
     *
     * /api/minecraft/install/tasks
     */
    @GetMapping("/tasks")
    public Collection<MinecraftInstallTask> getTasks(){


        return installManager
                .getTasks()
                .values();


    }









    /**
     * 安装状态
     *
     * GET
     *
     * /api/minecraft/install/task/{id}/status
     */
    @GetMapping("/task/{id}/status")
    public Map<String,Object> status(
            @PathVariable String id
    ){



        MinecraftInstallTask task =

                installManager.getTask(
                        id
                );




        if(task == null){


            return Map.of(

                    "exists",
                    false

            );


        }







        return Map.of(

                "exists",
                true,

                "id",
                task.getId(),

                "status",
                task.getStatus(),

                "progress",
                task.getProgress(),

                "step",
                task.getStep(),

                "currentFile",
                task.getCurrentFile()

        );


    }



}