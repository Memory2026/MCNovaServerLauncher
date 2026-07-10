package com.xingci.mcnsl.minecraft.instance;


import com.xingci.mcnsl.minecraft.launch.LaunchOptions;
import com.xingci.mcnsl.minecraft.launch.MinecraftLauncher;
import com.xingci.mcnsl.minecraft.launch.ProcessManager;


import org.springframework.web.bind.annotation.*;


import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Minecraft实例控制器
 *
 * 管理:
 *
 * 创建
 * 查询
 * 删除
 * 启动
 */
@RestController
@RequestMapping("/api/instances")
@CrossOrigin
public class MinecraftInstanceController {



    private final MinecraftInstanceManager instanceManager;


    private final MinecraftLauncher launcher;


    private final ProcessManager processManager;





    public MinecraftInstanceController(
            MinecraftInstanceManager instanceManager,
            MinecraftLauncher launcher,
            ProcessManager processManager
    ){

        this.instanceManager =
                instanceManager;


        this.launcher =
                launcher;


        this.processManager =
                processManager;

    }









    /**
     * 获取所有实例
     *
     * GET /api/instances
     */
    @GetMapping
    public List<MinecraftInstance> list()
            throws Exception {


        return instanceManager
                .list();

    }









    /**
     * 获取实例
     */
    @GetMapping("/{id}")
    public MinecraftInstance get(
            @PathVariable String id
    )
            throws Exception {


        return instanceManager
                .get(id);

    }









    /**
     * 创建实例
     *
     * POST:
     *
     * {
     *  name:"生存服",
     *  version:"1.21.8",
     *  loader:"fabric"
     * }
     */
    @PostMapping
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
     * 删除实例
     */
    @DeleteMapping("/{id}")
    public boolean delete(
            @PathVariable String id
    )
            throws Exception {


        return instanceManager
                .delete(
                        id
                );


    }









    /**
     * 修改实例
     */
    @PutMapping("/{id}")
    public MinecraftInstance update(
            @PathVariable String id,

            @RequestBody MinecraftInstance update
    )
            throws Exception {



        MinecraftInstance old =
                instanceManager
                        .get(id);





        if(old==null){


            throw new RuntimeException(
                    "实例不存在"
            );


        }





        if(update.getName()!=null){

            old.setName(
                    update.getName()
            );

        }




        if(update.getJavaPath()!=null){

            old.setJavaPath(
                    update.getJavaPath()
            );

        }




        if(update.getMaxMemory()>0){

            old.setMaxMemory(
                    update.getMaxMemory()
            );

        }




        if(update.getMinMemory()>0){

            old.setMinMemory(
                    update.getMinMemory()
            );

        }




        instanceManager.save(
                old
        );




        return old;


    }









    /**
     * 启动实例
     */
    @PostMapping("/{id}/launch")
    public Map<String,String> launch(
            @PathVariable String id
    )
            throws Exception {



        MinecraftInstance instance =

                instanceManager
                        .get(id);





        if(instance==null){


            throw new RuntimeException(
                    "实例不存在"
            );


        }








        String processId =

                UUID
                        .randomUUID()
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

                                instance.getJavaPath()==null
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

                System.out::println

        );






        return Map.of(

                "id",
                processId,

                "status",
                "started"

        );


    }









    /**
     * 查询实例是否运行
     */
    @GetMapping("/{id}/running")
    public boolean running(
            @PathVariable String id
    ){



        return processManager
                .isRunning(
                        id
                );


    }


}