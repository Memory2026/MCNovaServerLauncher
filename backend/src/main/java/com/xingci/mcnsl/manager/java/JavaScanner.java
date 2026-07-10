package com.xingci.mcnsl.manager.java;


import com.xingci.mcnsl.model.JavaInfo;

import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.*;



/**
 * Java扫描器
 *
 * 自动发现系统Java
 */
@Component
public class JavaScanner {



    /**
     * 扫描Java环境
     */
    public List<JavaInfo> scan(){


        Map<String, JavaInfo> result =
                new HashMap<>();



        /*
         * JAVA_HOME
         */
        scanHome(
                System.getenv("JAVA_HOME"),
                result
        );



        /*
         * PATH中的java
         */
        scanCommand(
                "java",
                result
        );



        /*
         * 常见目录
         */
        scanCommonLocations(
                result
        );



        return new ArrayList<>(
                result.values()
        );

    }









    /**
     * 扫描JAVA_HOME
     */
    private void scanHome(
            String home,
            Map<String, JavaInfo> result
    ){


        if(home==null){

            return;

        }


        Path java =
                getJavaExecutable(
                        Path.of(home)
                );


        add(
                java,
                result
        );


    }









    /**
     * 扫描命令
     */
    private void scanCommand(
            String command,
            Map<String, JavaInfo> result
    ){


        try{


            Process process =
                    new ProcessBuilder(
                            command,
                            "-version"
                    )
                            .redirectErrorStream(true)
                            .start();



            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );



            String line;



            while(
                    (line=reader.readLine())
                            !=null
            ){



                if(line.contains("version")){


                    String path =
                            findCommandPath(
                                    command
                            );


                    add(
                            Path.of(path),
                            result
                    );


                    break;

                }


            }


        }
        catch(Exception ignored){


        }


    }









    /**
     * 扫描默认目录
     */
    private void scanCommonLocations(
            Map<String, JavaInfo> result
    ){


        String os =
                System.getProperty(
                                "os.name"
                        )
                        .toLowerCase();




        List<Path> paths =
                new ArrayList<>();




        if(os.contains("win")){


            paths.add(
                    Path.of(
                            "C:\\Program Files\\Java"
                    )
            );


            paths.add(
                    Path.of(
                            "C:\\Program Files\\Eclipse Adoptium"
                    )
            );


        }



        else if(os.contains("mac")){


            paths.add(
                    Path.of(
                            "/Library/Java/JavaVirtualMachines"
                    )
            );


        }



        else{


            paths.add(
                    Path.of(
                            "/usr/lib/jvm"
                    )
            );


        }






        for(Path root:paths){


            if(!Files.exists(root)){


                continue;

            }





            try{


                Files.walk(root,3)

                        .filter(
                                Files::isDirectory
                        )

                        .forEach(
                                dir -> {


                                    Path java =
                                            getJavaExecutable(
                                                    dir
                                            );


                                    add(
                                            java,
                                            result
                                    );


                                }
                        );


            }
            catch(IOException ignored){


            }



        }


    }









    /**
     * 添加Java
     */
    private void add(
            Path java,
            Map<String, JavaInfo> result
    ){


        if(java==null
                ||
                !Files.exists(java)){


            return;


        }




        try{


            JavaInfo info =
                    parse(
                            java
                    );



            result.put(
                    java.toAbsolutePath()
                            .toString(),

                    info
            );



        }
        catch(Exception ignored){


        }


    }









    /**
     * 获取Java版本
     */
    private JavaInfo parse(
            Path java
    )
            throws Exception {



        Process process =
                new ProcessBuilder(

                        java.toString(),

                        "-version"

                )

                        .redirectErrorStream(true)

                        .start();





        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                process.getInputStream()
                        )
                );




        String version =
                reader.readLine();





        int major =
                parseMajor(
                        version
                );





        return new JavaInfo(

                java,

                version,

                major

        );


    }









    /**
     * 解析主版本
     */
    private int parseMajor(
            String text
    ){


        if(text==null){

            return 0;

        }



        String[] nums =
                text.replace(
                                "\"",
                                ""
                        )
                        .split("\\.");




        try{


            int first =
                    Integer.parseInt(
                            nums[0]
                    );



            if(first==1){

                return Integer.parseInt(
                        nums[1]
                );

            }


            return first;


        }
        catch(Exception e){


            return 0;


        }


    }









    /**
     * 获取java执行文件
     */
    private Path getJavaExecutable(
            Path home
    ){


        if(home==null){

            return null;

        }



        Path bin =
                home.resolve(
                        "bin"
                );



        String exe =
                System.getProperty(
                                "os.name"
                        )
                        .toLowerCase()
                        .contains("win")
                        ?
                        "java.exe"
                        :
                        "java";



        Path java =
                bin.resolve(
                        exe
                );



        if(Files.exists(java)){


            return java;


        }



        return null;


    }









    /**
     * 获取PATH中的真实路径
     */
    private String findCommandPath(
            String command
    )
    {


        try{


            Process process =
                    new ProcessBuilder(

                            System.getProperty(
                                            "os.name"
                                    )
                                    .toLowerCase()
                                    .contains("win")
                                    ?
                                    "where"
                                    :
                                    "which",

                            command

                    )

                            .start();




            return new BufferedReader(

                    new InputStreamReader(
                            process.getInputStream()
                    )

            )
                    .readLine();


        }
        catch(Exception e){


            return null;


        }


    }



}