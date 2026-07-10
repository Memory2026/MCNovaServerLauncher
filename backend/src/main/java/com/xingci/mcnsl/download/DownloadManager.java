package com.xingci.mcnsl.download;


import org.springframework.stereotype.Service;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;



/**
 * 普通文件下载管理器
 *
 * 用于：
 *
 * - Mod下载
 * - 文件下载
 * - 网络资源下载
 *
 */
@Service
public class DownloadManager {



    private static final int MAX_DOWNLOAD =
            3;



    private final ExecutorService executor =
            Executors.newFixedThreadPool(
                    MAX_DOWNLOAD
            );



    private final Map<String, DownloadTask> tasks =
            new ConcurrentHashMap<>();



    private final Map<String, Future<?>> futures =
            new ConcurrentHashMap<>();





    /**
     * 提交下载任务
     */
    public DownloadTask submit(
            String name,
            String url,
            Path target
    ){


        DownloadTask task =
                new DownloadTask(
                        name
                );


        task.setUrl(url);

        task.setTarget(target);



        tasks.put(
                task.getId(),
                task
        );



        Future<?> future =
                executor.submit(
                        () -> {

                            try{

                                new FileDownloader()
                                        .download(
                                                task
                                        );


                            }catch(Exception e){

                                task.fail(
                                        e.getMessage()
                                );

                            }

                        }
                );



        futures.put(
                task.getId(),
                future
        );



        return task;

    }







    /**
     * 获取任务
     */
    public DownloadTask get(
            String id
    ){

        return tasks.get(id);

    }







    /**
     * 获取全部任务
     */
    public List<DownloadTask> getAll(){

        return new ArrayList<>(
                tasks.values()
        );

    }







    /**
     * 取消任务
     */
    public boolean cancel(
            String id
    ){


        DownloadTask task =
                tasks.get(id);



        if(task == null){

            return false;

        }



        Future<?> future =
                futures.get(id);



        if(future != null){

            future.cancel(
                    true
            );

        }



        task.cancel();



        return true;

    }








    /**
     * 删除已完成任务
     */
    public void clearFinished(){


        tasks.values()
                .removeIf(

                        task ->
                                task.getStatus()
                                        ==
                                        DownloadStatus.COMPLETED

                );


    }








    /**
     * 当前运行数量
     */
    public long runningCount(){


        return tasks.values()
                .stream()
                .filter(
                        DownloadTask::isRunning
                )
                .count();

    }








    /**
     * 关闭线程池
     */
    public void shutdown(){


        executor.shutdownNow();


    }


}