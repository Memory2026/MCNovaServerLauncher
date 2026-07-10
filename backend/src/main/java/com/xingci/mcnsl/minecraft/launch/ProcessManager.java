package com.xingci.mcnsl.minecraft.launch;


import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Minecraft进程管理器
 */
@Component
public class ProcessManager {



    /**
     * 正在运行的进程
     */
    private final Map<String, Process> processes =

            new ConcurrentHashMap<>();









    /**
     * 注册进程
     */
    public void register(
            String id,
            Process process
    ){


        processes.put(

                id,

                process

        );


    }









    /**
     * 获取进程
     */
    public Process get(
            String id
    ){


        return processes.get(
                id
        );


    }









    /**
     * 判断是否运行
     */
    public boolean isRunning(
            String id
    ){


        Process process =

                processes.get(
                        id
                );





        if(process == null){


            return false;


        }






        return process.isAlive();


    }









    /**
     * 停止进程
     */
    public boolean stop(
            String id
    ){



        Process process =

                processes.remove(
                        id
                );






        if(process == null){


            return false;


        }







        if(process.isAlive()){


            process.destroy();




            return true;


        }







        return false;


    }









    /**
     * 删除记录
     */
    public void remove(
            String id
    ){


        processes.remove(
                id
        );


    }









    /**
     * 清理退出进程
     */
    public void cleanup(){



        processes.entrySet()

                .removeIf(

                        entry ->

                                !entry.getValue()
                                        .isAlive()

                );


    }









    /**
     * 获取运行数量
     */
    public int runningCount(){



        return (int)

                processes.values()

                        .stream()

                        .filter(
                                Process::isAlive
                        )

                        .count();


    }



}