package com.xingci.mcnsl.manager.java;


import com.xingci.mcnsl.model.JavaInfo;

import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;


/**
 * Java release 文件解析器
 *
 * 解析:
 *
 * JAVA_HOME/release
 *
 */
@Component
public class ReleaseParser {



    /**
     * 解析 Java 信息
     */
    public JavaInfo parse(
            Path javaHome
    ){


        Path release =

                javaHome.resolve("release");



        String version =
                "Unknown";

        String vendor =
                "Unknown";





        if(!release.toFile().exists()){


            return new JavaInfo(

                    javaHome,

                    version,

                    detectMajor(version)

            );


        }







        try(
                BufferedReader reader =
                        new BufferedReader(

                                new java.io.FileReader(
                                        release.toFile()
                                )

                        )
        ){


            String line;



            while(
                    (line = reader.readLine())

                            != null
            ){


                if(line.startsWith(
                        "JAVA_VERSION="
                )){


                    version =
                            clean(line);


                }



                if(line.startsWith(
                        "IMPLEMENTOR="
                )){


                    vendor =
                            clean(line);


                }


            }



        }
        catch(IOException ignored){

        }







        int major =

                detectMajor(version);






        return new JavaInfo(

                javaHome,

                version,

                major

        );


    }









    private String clean(
            String line
    ){


        int index =
                line.indexOf('=');



        if(index < 0){

            return "Unknown";

        }



        return line.substring(
                        index + 1
                )
                .replace("\"","")
                .trim();


    }









    /**
     * Java版本号转换
     *
     * 例如:
     *
     * 1.8.0_402 -> 8
     *
     * 17.0.12 ->17
     *
     */
    private int detectMajor(
            String version
    ){


        try{


            version =
                    version.replace("\"","");



            if(version.startsWith("1.")){


                return Integer.parseInt(

                        version.split("\\.")[1]

                );


            }



            return Integer.parseInt(

                    version.split("\\.")[0]

            );



        }
        catch(Exception e){


            return 0;


        }


    }


}