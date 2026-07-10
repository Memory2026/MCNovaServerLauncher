package com.xingci.mcnsl.minecraft.launch;


import com.xingci.mcnsl.minecraft.version.MinecraftVersion;


import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



/**
 * Minecraft启动命令构建器
 *
 * 负责生成:
 *
 * java
 * JVM参数
 * classpath
 * MainClass
 * 游戏参数
 */
@Component
public class LaunchCommandBuilder {



    private final ClasspathBuilder classpathBuilder;





    public LaunchCommandBuilder(
            ClasspathBuilder classpathBuilder
    ){

        this.classpathBuilder =
                classpathBuilder;

    }









    /**
     * 构建启动命令
     */
    public List<String> build(
            Path java,

            Path gameDir,

            MinecraftVersion version,

            LaunchOptions options,

            List<String> jvmArgs,

            List<String> gameArgs

    )
            throws IOException
    {



        List<String> command =

                new ArrayList<>();








        /*
         * Java路径
         */
        command.add(

                java

                        .toAbsolutePath()

                        .toString()

        );








        /*
         * 内存
         */
        command.add(

                "-Xms"
                        +
                        options.getMinMemory()
                        +
                        "M"

        );



        command.add(

                "-Xmx"
                        +
                        options.getMaxMemory()
                        +
                        "M"

        );








        /*
         * natives
         */
        command.add(

                "-Djava.library.path="

                        +

                        gameDir

                                .resolve(
                                        "natives"
                                )

                                .toAbsolutePath()

                                .toString()

        );








        /*
         * JVM参数
         */
        if(jvmArgs != null
                &&
                !jvmArgs.isEmpty()){


            command.addAll(

                    jvmArgs

            );


        }









        /*
         * ClassPath
         */
        command.add(
                "-cp"
        );





        command.add(

                String.join(

                        System.getProperty(
                                "path.separator"
                        ),

                        classpathBuilder.build(

                                        version,

                                        gameDir

                                )

                                .stream()

                                .map(
                                        Path::toString
                                )

                                .toList()

                )

        );









        /*
         * MainClass
         */
        String mainClass =

                version.getMainClass();





        if(mainClass == null
                ||
                mainClass.isBlank()){


            mainClass =
                    "net.minecraft.client.main.Main";


        }





        command.add(
                mainClass
        );









        /*
         * 游戏参数
         */
        if(gameArgs != null
                &&
                !gameArgs.isEmpty()){


            command.addAll(
                    gameArgs
            );


        }







        return command;


    }



}