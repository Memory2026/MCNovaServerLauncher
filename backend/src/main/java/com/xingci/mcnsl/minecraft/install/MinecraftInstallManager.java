package com.xingci.mcnsl.minecraft.install;


import com.xingci.mcnsl.minecraft.download.MinecraftDownloader;
import com.xingci.mcnsl.minecraft.download.MinecraftInstallTask;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstance;
import com.xingci.mcnsl.minecraft.instance.MinecraftInstanceManager;

import com.xingci.mcnsl.minecraft.download.InstallStatus;
import com.xingci.mcnsl.minecraft.loader.FabricInstaller;
import com.xingci.mcnsl.websocket.MinecraftProgressSocket;

import org.springframework.stereotype.Component;


import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Minecraft安装管理器
 *
 * 负责:
 *
 * 创建安装任务
 * 管理安装状态
 * 调用下载流程
 */
@Component
public class MinecraftInstallManager {



    private final MinecraftDownloader downloader;


    private final MinecraftInstanceManager instanceManager;

    private final MinecraftProgressSocket progressSocket;

    private final FabricInstaller fabricInstaller;



    /**
     * 安装任务列表
     */
    private final Map<String, MinecraftInstallTask> tasks =

            new ConcurrentHashMap<>();









    public MinecraftInstallManager(
            MinecraftDownloader downloader,
            MinecraftInstanceManager instanceManager,
            MinecraftProgressSocket progressSocket,
            FabricInstaller fabricInstaller
    ){


        this.downloader =
                downloader;


        this.instanceManager =
                instanceManager;

        this.progressSocket =
                progressSocket;

        this.fabricInstaller =
                fabricInstaller;


    }









    /**
     * 安装实例
     */
    public MinecraftInstallTask installInstance(
            String id
    )
            throws Exception {

        return installInstance(
                id,
                null
        );

    }









    /**
     * 安装实例，可附带加载器版本
     */
    public MinecraftInstallTask installInstance(
            String id,
            String loaderVersion
    )
            throws Exception {



        MinecraftInstance instance =

                instanceManager.get(
                        id
                );





        if(instance == null){


            throw new RuntimeException(
                    "实例不存在"
            );


        }








        MinecraftInstallTask task =

                new MinecraftInstallTask();





        task.setVersion(

                instance.getVersion()

        );

        task.setListener(
                () -> progressSocket.send(task)
        );





        tasks.put(

                task.getId(),

                task

        );







        Path gameDir =

                Path.of(

                        instance.getPath()

                );







        /*
         * 异步安装
         */
        Thread thread =

                new Thread(

                        () -> {


                            try {



                                downloader.download(

                                        instance.getVersion(),

                                        gameDir,

                                        task

                                );

                                installLoaderIfNeeded(
                                        instance,
                                        loaderVersion,
                                        gameDir,
                                        task
                                );

                                instance.setStatus("INSTALLED");
                                instanceManager.save(instance);

                                task.success();



                            }
                            catch(Exception e){



                                task.failed(

                                        e.getMessage()

                                );


                            }


                        }

                );







        thread.setName(

                "Minecraft-Install-"
                        +
                        task.getId()

        );



        thread.start();







        return task;


    }









    private void installLoaderIfNeeded(
            MinecraftInstance instance,
            String loaderVersion,
            Path gameDir,
            MinecraftInstallTask task
    )
            throws Exception {

        String loader =
                instance.getLoader();

        if(loader == null
                ||
                loader.isBlank()
                ||
                "vanilla".equalsIgnoreCase(loader)) {

            return;

        }

        if(loaderVersion == null
                ||
                loaderVersion.isBlank()
                ||
                loaderVersion.startsWith("暂无")) {

            throw new RuntimeException("未选择可用的 " + loader + " 版本");

        }

        if("fabric".equalsIgnoreCase(loader)) {

            task.setStep("安装 Fabric Loader");
            task.setCurrentFile(loaderVersion);

            String minecraftVersion =
                    instance.getVersion();

            fabricInstaller.install(
                    minecraftVersion,
                    loaderVersion,
                    gameDir
            );

            instance.setVersion(
                    "fabric-loader-"
                            +
                            loaderVersion
                            +
                            "-"
                            +
                            minecraftVersion
            );

            return;

        }

        throw new RuntimeException(loader + " 安装器暂未接入，仅支持版本检测与匹配");

    }









    /**
     * 获取任务
     */
    public MinecraftInstallTask getTask(
            String id
    ){


        return tasks.get(id);


    }









    /**
     * 获取全部任务
     */
    public Map<String, MinecraftInstallTask> getTasks(){

        return tasks;

    }









    /**
     * 删除完成任务
     */
    public void clearFinished(){


        tasks.entrySet()

                .removeIf(

                        entry -> {


                            MinecraftInstallTask task =

                                    entry.getValue();



                            return task.getStatus()
                                    ==
                                    InstallStatus.SUCCESS

                                    ||

                                    task.getStatus()
                                            ==
                                            InstallStatus.FAILED;


                        }

                );


    }


}
