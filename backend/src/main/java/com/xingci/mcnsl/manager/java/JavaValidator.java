package com.xingci.mcnsl.manager.java;


import com.xingci.mcnsl.model.JavaInfo;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;



@Component
public class JavaValidator {


    /**
     * 验证 Java Home
     */
    public JavaInfo validate(Path javaHome) {


        if (javaHome == null ||
                !Files.exists(javaHome) ||
                !Files.isDirectory(javaHome)) {

            return null;

        }


        boolean windows =
                System.getProperty("os.name")
                        .toLowerCase()
                        .contains("win");



        Path javaExecutable =
                javaHome.resolve("bin")
                        .resolve(
                                windows
                                        ? "java.exe"
                                        : "java"
                        );


        if (!Files.exists(javaExecutable)) {

            return null;

        }



        Path javacExecutable =
                javaHome.resolve("bin")
                        .resolve(
                                windows
                                        ? "javac.exe"
                                        : "javac"
                        );



        JavaInfo info = new JavaInfo();



        info.setPath(
                javaHome
                        .toAbsolutePath()
                        .toString()
        );


        info.setJavaExecutable(
                javaExecutable
                        .toAbsolutePath()
                        .toString()
        );



        if(Files.exists(javacExecutable)){


            info.setJavacExecutable(
                    javacExecutable
                            .toAbsolutePath()
                            .toString()
            );


            info.setJdk(true);


        }else{


            info.setJdk(false);

        }



        /*
         * 获取 Java 版本
         */
        try {


            Process process =
                    new ProcessBuilder(
                            javaExecutable.toString(),
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


            String versionLine = null;


            String line;


            while((line = reader.readLine()) != null){


                if(line.contains("version")){

                    versionLine = line;

                    break;

                }

            }


            process.waitFor();



            if(versionLine != null){


                /*
                 * 示例:
                 *
                 * java version "21.0.11" 2026-04-21 LTS
                 *
                 */


                String version =
                        versionLine
                                .replace("\"","")
                                .trim();



                info.setVersion(version);



                String major;


                if(version.startsWith("1.8")){

                    major = "8";

                }
                else {


                    major =
                            version
                                    .split("\\.")[0];

                }



                info.setName(
                        "Java " + major
                );


            }


        }catch(Exception e){


            info.setName(
                    "Java"
            );


            info.setVersion(
                    "Unknown"
            );

        }




        /*
         * 判断当前 Java
         */
        String currentHome =
                System.getProperty("java.home");



        if(currentHome != null){


            if(javaHome
                    .toAbsolutePath()
                    .normalize()
                    .equals(
                            Path.of(currentHome)
                                    .toAbsolutePath()
                                    .normalize()
                    )){


                info.setCurrent(true);

            }

        }



        return info;

    }

}