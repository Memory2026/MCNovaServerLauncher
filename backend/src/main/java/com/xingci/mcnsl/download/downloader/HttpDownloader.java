package com.xingci.mcnsl.download.downloader;

import com.xingci.mcnsl.download.DownloadResult;
import com.xingci.mcnsl.download.DownloadTask;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * HTTP 下载器
 *
 * 负责：
 * 1. 下载网络文件
 * 2. 更新下载进度
 * 3. 统计速度
 */
@Component
public class HttpDownloader {


    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 8192;



    /**
     * 下载文件
     *
     * @param task 下载任务
     * @param url 文件地址
     * @param target 保存位置
     */
    public DownloadResult download(
            DownloadTask task,
            String url,
            Path target
    ){

        LocalDateTime start =
                LocalDateTime.now();


        try {


            task.setStep("连接服务器");

            task.setCurrentFile(
                    target.getFileName().toString()
            );


            // 创建目录

            Path parent =
                    target.getParent();

            if(parent != null){

                Files.createDirectories(parent);

            }



            HttpURLConnection connection =
                    (HttpURLConnection)
                            URI.create(url)
                                    .toURL()
                                    .openConnection();


            connection.setRequestMethod("GET");

            connection.setConnectTimeout(
                    10000
            );

            connection.setReadTimeout(
                    30000
            );

            connection.connect();



            int code =
                    connection.getResponseCode();


            if(code != HttpURLConnection.HTTP_OK){

                return DownloadResult.failed(
                        task,
                        "HTTP错误: "
                                + code
                );
            }



            long total =
                    connection.getContentLengthLong();



            task.setStep(
                    "正在下载"
            );


            long downloaded = 0;


            long lastBytes = 0;

            long lastTime =
                    System.currentTimeMillis();



            try(
                    InputStream input =
                            connection.getInputStream();

                    OutputStream output =
                            Files.newOutputStream(target)
            ){


                byte[] buffer =
                        new byte[BUFFER_SIZE];


                int length;



                while(
                        (length =
                                input.read(buffer))
                                != -1
                ){


                    output.write(
                            buffer,
                            0,
                            length
                    );


                    downloaded += length;



                    task.update(
                            downloaded,
                            total
                    );



                    /*
                     * 计算速度
                     */

                    long now =
                            System.currentTimeMillis();


                    long diff =
                            now - lastTime;


                    if(diff >= 1000){

                        long speed =
                                (downloaded - lastBytes)
                                        * 1000
                                        /
                                        diff;


                        task.updateSpeed(
                                speed
                        );


                        lastBytes =
                                downloaded;

                        lastTime =
                                now;
                    }


                    // 支持取消

                    if(
                            Thread.currentThread()
                                    .isInterrupted()
                    ){

                        throw new RuntimeException(
                                "下载取消"
                        );
                    }

                }

            }



            Duration duration =
                    Duration.between(
                            start,
                            LocalDateTime.now()
                    );



            task.setStep(
                    "完成"
            );


            return DownloadResult.success(
                    task,
                    target,
                    downloaded,
                    duration,
                    false
            );



        } catch(Exception e){


            task.fail(
                    e.getMessage()
            );


            return DownloadResult.failed(
                    task,
                    e
            );

        }

    }

}