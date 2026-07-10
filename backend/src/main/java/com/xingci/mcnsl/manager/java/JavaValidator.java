package com.xingci.mcnsl.manager.java;


import com.xingci.mcnsl.model.JavaInfo;

import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Java环境验证器
 *
 * 负责：
 *
 * 1. 判断Java目录
 * 2. 判断JDK/JRE
 * 3. 获取版本
 * 4. 校验Java环境
 */
@Component
public class JavaValidator {



    /**
     * 验证Java Home
     *
     * @param home Java目录
     */
    public boolean validate(
            Path home
    ){


        return findJava(home) != null;


    }









    /**
     * 创建JavaInfo
     */
    public JavaInfo inspect(
            Path home
    )
    {


        Path java =
                findJava(home);



        if(java == null){


            throw new IllegalArgumentException(
                    "不是有效Java目录:"
                            +
                            home
            );


        }



        try{


            String version =
                    getVersion(java);



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
        catch(Exception e){


            throw new RuntimeException(
                    "Java检测失败",
                    e
            );


        }


    }









    /**
     * 查找java执行文件
     */
    public Path findJava(
            Path home
    ){


        if(home == null
                ||
                !Files.exists(home)){


            return null;


        }






        Path bin =
                home.resolve(
                        "bin"
                );





        String javaName =
                isWindows()
                        ?
                        "java.exe"
                        :
                        "java";





        Path java =
                bin.resolve(
                        javaName
                );






        if(Files.exists(java)
                &&
                Files.isExecutable(java)){


            return java;


        }




        return null;


    }









    /**
     * 查找javac
     *
     * 判断JDK
     */
    public Path findJavac(
            Path home
    ){


        if(home == null){


            return null;


        }




        Path javac =

                home
                        .resolve("bin")
                        .resolve(
                                isWindows()
                                        ?
                                        "javac.exe"
                                        :
                                        "javac"
                        );





        if(Files.exists(javac)){


            return javac;


        }




        return null;


    }









    /**
     * 判断是否JDK
     */
    public boolean isJdk(
            Path home
    ){


        return findJavac(
                home
        ) != null;


    }









    /**
     * 获取版本信息
     */
    public String getVersion(
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






        String line;



        while(
                (line = reader.readLine())
                        != null
        ){


            if(line.contains(
                    "version"
            )){


                return line;


            }


        }



        return "unknown";


    }









    /**
     * 获取主版本
     */
    public int getMajorVersion(
            Path java
    )
            throws Exception {



        return parseMajor(
                getVersion(
                        java
                )
        );


    }









    /**
     * 判断Minecraft是否适用
     */
    public boolean supports(
            JavaInfo java,
            String minecraftVersion
    ){


        int required =
                requiredJava(
                        minecraftVersion
                );



        return java.getMajorVersion()
                >=
                required;


    }









    /**
     * Minecraft需要的Java版本
     */
    private int requiredJava(
            String version
    ){


        try{


            String[] arr =
                    version.split("\\.");



            int major =
                    Integer.parseInt(
                            arr[1]
                    );



            int minor =
                    arr.length > 2
                            ?
                            Integer.parseInt(
                                    arr[2]
                            )
                            :
                            0;





            if(major >= 21){

                return 21;

            }





            if(major >= 17){

                return 17;

            }





            return 8;


        }
        catch(Exception e){


            return 17;


        }


    }









    /**
     * 解析版本号
     */
    private int parseMajor(
            String text
    ){


        if(text == null){

            return 0;

        }



        try{


            String value =
                    text.replace(
                            "\"",
                            ""
                    );



            String[] arr =
                    value.split("\\.");



            int first =
                    Integer.parseInt(
                            arr[0]
                    );




            if(first == 1){


                return Integer.parseInt(
                        arr[1]
                );


            }




            return first;


        }
        catch(Exception e){


            return 0;


        }


    }









    private boolean isWindows(){


        return System.getProperty(
                        "os.name"
                )
                .toLowerCase()
                .contains(
                        "win"
                );


    }


}