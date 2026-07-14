package com.xingci.mcnsl.minecraft.launch;


import com.xingci.mcnsl.minecraft.version.MinecraftVersion;
import com.xingci.mcnsl.minecraft.version.VersionParser;


import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;



/**
 * Minecraft启动器
 *
 * 负责:
 *
 * 1. 构建启动命令
 * 2. 创建Minecraft进程
 * 3. 输出游戏日志
 * 4. 注册进程管理
 *
 */
@Component
public class MinecraftLauncher {



    private final ClasspathBuilder classpathBuilder;


    private final ArgumentBuilder argumentBuilder;


    private final JavaCommandBuilder commandBuilder;


    private final VersionParser versionParser;


    private final ProcessManager processManager;








    public MinecraftLauncher(
            ClasspathBuilder classpathBuilder,
            ArgumentBuilder argumentBuilder,
            JavaCommandBuilder commandBuilder,
            VersionParser versionParser,
            ProcessManager processManager
    ){


        this.classpathBuilder =
                classpathBuilder;


        this.argumentBuilder =
                argumentBuilder;


        this.commandBuilder =
                commandBuilder;


        this.versionParser =
                versionParser;


        this.processManager =
                processManager;


    }









    /**
     * 启动Minecraft
     */
    public void launch(
            String processId,
            LaunchOptions options,
            Consumer<String> logger
    )
            throws Exception {



        Path gameDir =
                options.getGameDir();




        /*
         * 读取版本文件
         */
        Path versionJson =

                gameDir

                        .resolve("versions")

                        .resolve(
                                options.getVersion()
                        )

                        .resolve(
                                options.getVersion()
                                        +
                                        ".json"
                        );






        MinecraftVersion version =

                versionParser.parse(

                        versionJson,

                        gameDir

                );









        /*
         * 生成classpath
         */
        List<Path> classpath =

                classpathBuilder.build(

                        version,

                        gameDir

                );








        /*
         * 游戏参数
         */
        List<String> gameArgs =

                argumentBuilder.build(

                        version,

                        gameDir,

                        options.getUsername(),

                        options.getUuid(),

                        options.getAccessToken(),

                        options.getUserType()

                );









        /*
         * Java命令
         */
        List<String> command =

                commandBuilder.build(

                        options.getJavaPath(),

                        version,

                        classpath,

                        gameArgs,

                        options.getMinMemory(),

                        options.getMaxMemory()

                );








        logger.accept(
                "启动命令:"
        );


        logger.accept(

                String.join(
                        " ",
                        command
                )

        );









        ProcessBuilder builder =

                new ProcessBuilder(

                        command

                );




        builder.directory(

                gameDir.toFile()

        );



        builder.redirectErrorStream(
                true
        );







        Process process =

                builder.start();







        /*
         * 注册进程
         */
        processManager.register(

                processId,

                process

        );









        /*
         * 读取日志
         */
        Thread logThread =

                new Thread(

                        () -> readLog(

                                process,

                                logger

                        )

                );



        logThread.setName(

                "Minecraft-Log-Reader"

        );



        logThread.start();








        /*
         * 等待结束
         */
        Thread waitThread =

                new Thread(

                        () -> {


                            try {


                                int exitCode =

                                        process.waitFor();



                                logger.accept(

                                        "Minecraft退出:"
                                                +
                                                exitCode

                                );



                                processManager.remove(

                                        processId

                                );


                            }
                            catch(Exception e){


                                logger.accept(

                                        e.getMessage()

                                );


                            }



                        }

                );



        waitThread.setName(

                "Minecraft-Wait"

        );


        waitThread.start();


    }









    /**
     * 读取Minecraft输出
     */
    private void readLog(
            Process process,
            Consumer<String> logger
    ){


        try(

                BufferedReader reader =

                        new BufferedReader(

                                new InputStreamReader(

                                        process.getInputStream()

                                ),

                                1

                        )

        ){


            String line;


            while(

                    (line = reader.readLine())

                            != null

            ){


                logger.accept(
                        line
                );


            }


        }
        catch(Exception e){


            logger.accept(

                    "日志读取失败:"
                            +
                            e.getMessage()

            );


        }


    }


}