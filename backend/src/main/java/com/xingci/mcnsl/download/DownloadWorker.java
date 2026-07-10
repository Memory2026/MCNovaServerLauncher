package com.xingci.mcnsl.download;

import com.xingci.mcnsl.util.HashUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * 下载执行器
 *
 * 一个 Worker 负责执行一个 DownloadTask
 */
public class DownloadWorker implements Callable<DownloadResult> {


    /**
     * 下载任务
     */
    private final DownloadTask task;


    public DownloadWorker(DownloadTask task) {
        this.task = task;
    }


    @Override
    public DownloadResult call() {


        LocalDateTime start = LocalDateTime.now();


        try {

            task.start();


            DownloadRequest request = task.getRequest();


            if (request == null) {

                throw new IllegalStateException(
                        "DownloadRequest不能为空"
                );
            }


            Path target = request.getTarget();


            // 创建目录
            createParent(target);


            /*
             * 检查已有文件
             */
            if (!request.isOverwrite()
                    && Files.exists(target)) {


                if (verify(target, request)) {


                    long size =
                            Files.size(target);


                    task.finish();


                    return DownloadResult.success(
                            task,
                            target,
                            size,
                            Duration.between(
                                    start,
                                    LocalDateTime.now()
                            ),
                            true
                    );
                }
            }



            int retry = request.getRetry();


            Exception last = null;


            for (int i = 0; i < retry; i++) {

                try {

                    download(request, target);


                    if (!verify(target, request)) {

                        throw new IOException(
                                "文件校验失败"
                        );
                    }


                    long size =
                            Files.size(target);


                    task.finish();


                    return DownloadResult.success(
                            task,
                            target,
                            size,
                            Duration.between(
                                    start,
                                    LocalDateTime.now()
                            ),
                            false
                    );


                } catch (Exception e) {

                    last = e;

                }

            }


            throw last;


        } catch (Exception e) {


            task.fail(
                    e.getMessage()
            );


            return DownloadResult.failed(
                    task,
                    e.getMessage()
            );
        }

    }



    /**
     * 执行 HTTP 下载
     */
    private void download(
            DownloadRequest request,
            Path target
    ) throws Exception {


        task.updateCurrentFile(
                target.getFileName().toString()
        );


        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(
                                        request.getUrl()
                                )
                                .toURL()
                                .openConnection();



        connection.setRequestMethod(
                "GET"
        );


        connection.setConnectTimeout(
                10000
        );


        connection.setReadTimeout(
                30000
        );


        int code =
                connection.getResponseCode();



        if (code != HttpURLConnection.HTTP_OK) {

            throw new IOException(
                    "HTTP错误:" + code
            );
        }



        long total =
                connection.getContentLengthLong();



        task.update(
                0,
                total
        );



        try (
                InputStream input =
                        new BufferedInputStream(
                                connection.getInputStream()
                        );

                OutputStream output =
                        Files.newOutputStream(
                                target
                        )
        ) {



            byte[] buffer =
                    new byte[8192];


            long downloaded = 0;


            long lastTime =
                    System.currentTimeMillis();


            long lastBytes = 0;



            int length;



            while (
                    (length = input.read(buffer))
                            != -1
            ) {


                output.write(
                        buffer,
                        0,
                        length
                );


                downloaded += length;



                long now =
                        System.currentTimeMillis();



                task.update(
                        downloaded,
                        total
                );



                /*
                 * 速度计算
                 */
                if (now - lastTime >= 1000) {


                    long speed =
                            downloaded - lastBytes;


                    task.updateSpeed(
                            speed
                    );


                    lastTime = now;

                    lastBytes = downloaded;

                }


            }

        }


        finally {

            connection.disconnect();

        }

    }




    /**
     * 校验文件
     */
    private boolean verify(
            Path file,
            DownloadRequest request
    ) throws IOException {



        if (request.hasSize()) {

            if (Files.size(file)
                    != request.getSize()) {

                return false;
            }

        }



        if (request.hasSha1()) {

            return HashUtils.verifySha1(
                    file,
                    request.getSha1()
            );

        }



        if (request.hasSha256()) {

            return HashUtils.verifySha256(
                    file,
                    request.getSha256()
            );

        }



        return true;

    }





    /**
     * 创建父目录
     */
    private void createParent(
            Path path
    ) throws IOException {


        Path parent =
                path.getParent();


        if (parent != null) {

            Files.createDirectories(
                    parent
            );

        }

    }

}