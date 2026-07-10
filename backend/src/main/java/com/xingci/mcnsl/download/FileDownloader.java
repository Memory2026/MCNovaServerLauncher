package com.xingci.mcnsl.download;


import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.*;


/**
 * 文件下载器
 *
 * 执行实际文件传输
 */
@Component
public class FileDownloader {



    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE =
            1024 * 64;









    /**
     * 下载文件
     */
    public void download(
            DownloadTask task
    )
            throws Exception {



        Path target =
                task.getTarget();





        if(target.getParent()!=null){


            Files.createDirectories(
                    target.getParent()
            );


        }






        Path temp =

                target.resolveSibling(

                        target.getFileName()
                                +
                                ".download"

                );







        HttpURLConnection connection =

                (HttpURLConnection)

                        URI.create(
                                        task.getUrl()
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








        int size =
                connection.getContentLength();



        task.setTotalBytes(
                size
        );




        task.start();






        long start =
                System.currentTimeMillis();





        long lastBytes = 0;


        long lastTime = start;






        try(

                InputStream input =
                        connection.getInputStream();



                OutputStream output =
                        Files.newOutputStream(

                                temp,

                                StandardOpenOption.CREATE,

                                StandardOpenOption.TRUNCATE_EXISTING

                        )

        ){





            byte[] buffer =
                    new byte[
                            BUFFER_SIZE
                            ];



            int len;





            while(
                    (len=input.read(buffer))
                            != -1
            ){



                if(
                        Thread.currentThread()
                                .isInterrupted()
                ){


                    task.cancel();


                    return;


                }





                output.write(
                        buffer,
                        0,
                        len
                );





                long downloaded =

                        task.getDownloadedBytes()
                                +
                                len;




                task.setDownloadedBytes(
                        downloaded
                );







                /*
                 * 计算速度
                 */
                long now =
                        System.currentTimeMillis();





                if(now-lastTime>=1000){



                    long speed =

                            (downloaded-lastBytes)

                                    *

                                    1000

                                    /

                                    (now-lastTime);




                    task.setSpeed(
                            speed
                    );




                    lastBytes =
                            downloaded;


                    lastTime =
                            now;



                }



            }



        }







        /*
         * 下载完成
         */
        Files.move(

                temp,

                target,

                StandardCopyOption.REPLACE_EXISTING

        );





        task.finish();



    }


}