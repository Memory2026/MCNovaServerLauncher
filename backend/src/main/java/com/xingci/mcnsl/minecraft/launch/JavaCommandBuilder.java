package com.xingci.mcnsl.minecraft.launch;


import com.xingci.mcnsl.minecraft.version.MinecraftVersion;


import org.springframework.stereotype.Component;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



/**
 * Java启动命令生成器
 *
 * 负责生成完整 JVM 命令
 */
@Component
public class JavaCommandBuilder {



    /**
     * 构建启动命令
     *
     * @param javaPath Java路径
     * @param version Minecraft版本
     * @param classpath classpath列表
     * @param gameArgs 游戏参数
     * @param minMemory 最小内存
     * @param maxMemory 最大内存
     */
    public List<String> build(
            Path javaPath,
            MinecraftVersion version,
            List<Path> classpath,
            List<String> gameArgs,
            int minMemory,
            int maxMemory
    ){


        List<String> command =

                new ArrayList<>();





        /*
         * Java执行文件
         */
        if(javaPath != null){


            command.add(

                    javaPath
                            .toAbsolutePath()
                            .toString()

            );


        }
        else{


            command.add(
                    "java"
            );


        }








        /*
         * JVM内存
         */
        command.add(
                "-Xms"
                        +
                        minMemory
                        +
                        "M"
        );



        command.add(
                "-Xmx"
                        +
                        maxMemory
                        +
                        "M"
        );









        /*
         * 其他JVM参数
         */
        command.add(
                "-Djava.library.path="
                        +
                        Path.of("natives")
                                .toAbsolutePath()
        );

        if(version.getJvmArguments()!=null){


            command.addAll(

                    version.getJvmArguments()
                            .stream()
                            .filter(argument -> !argument.contains("${classpath}"))
                            .filter(argument -> !"-cp".equals(argument))
                            .map(argument -> argument.replace(
                                    "${natives_directory}",
                                    Path.of("natives")
                                            .toAbsolutePath()
                                            .toString()
                            ))
                            .toList()

            );


        }









        /*
         * classpath
         */
        command.add(
                "-cp"
        );



        command.add(

                buildClasspath(
                        classpath
                )

        );








        /*
         * 主类
         */
        command.add(

                version.getMainClass()

        );









        /*
         * 游戏参数
         */
        if(gameArgs!=null){


            command.addAll(

                    gameArgs

            );


        }







        return command;


    }









    /**
     * 拼接classpath
     */
    private String buildClasspath(
            List<Path> paths
    ){


        String separator =

                System
                        .getProperty(
                                "path.separator"
                        );




        StringBuilder builder =

                new StringBuilder();





        for(int i=0;i<paths.size();i++){



            builder.append(

                    paths.get(i)
                            .toAbsolutePath()
                            .toString()

            );



            if(i < paths.size()-1){


                builder.append(
                        separator
                );


            }


        }



        return builder.toString();


    }


}
