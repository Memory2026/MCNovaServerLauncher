package com.xingci.mcnsl.model;


import java.nio.file.Path;


/**
 * Java环境信息
 *
 * 用于Java管理器
 */
public class JavaInfo {



    /**
     * java可执行文件路径
     */
    private final Path javaPath;



    /**
     * Java版本字符串
     */
    private final String version;



    /**
     * 主版本
     *
     * 8
     * 17
     * 21
     */
    private final int majorVersion;



    /**
     * Java厂商
     */
    private String vendor;



    /**
     * javac路径
     */
    private Path javacPath;





    /**
     * 是否JDK
     */
    private boolean jdk;








    public JavaInfo(
            Path javaPath,
            String version,
            int majorVersion
    ){

        this.javaPath =
                javaPath;


        this.version =
                version;


        this.majorVersion =
                majorVersion;


        detect();


    }









    /**
     * 自动检测信息
     */
    private void detect(){



        Path home =
                getJavaHome();



        if(home==null){


            return;


        }






        /*
         * javac
         */
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





        if(java.nio.file.Files.exists(javac)){


            this.javacPath =
                    javac;


            this.jdk =
                    true;


        }





        /*
         * 厂商判断
         */
        String text =
                version
                        .toLowerCase();



        if(text.contains(
                "adoptium"
        )){


            vendor =
                    "Eclipse Adoptium";


        }
        else if(text.contains(
                "oracle"
        )){


            vendor =
                    "Oracle";


        }
        else if(text.contains(
                "openjdk"
        )){


            vendor =
                    "OpenJDK";


        }
        else{


            vendor =
                    "Unknown";


        }



    }









    /**
     * 获取JAVA_HOME
     */
    public Path getJavaHome(){



        if(javaPath==null){


            return null;


        }




        Path parent =
                javaPath.getParent();



        if(parent!=null
                &&
                parent.getFileName()
                        .toString()
                        .equals("bin")){


            return parent
                    .getParent();


        }




        return null;


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









    public Path getJavaPath(){

        return javaPath;

    }






    public String getVersion(){

        return version;

    }






    public int getMajorVersion(){

        return majorVersion;

    }






    public String getVendor(){

        return vendor;

    }






    public Path getJavacPath(){

        return javacPath;

    }






    public boolean isJdk(){

        return jdk;

    }






    @Override
    public String toString(){


        return "JavaInfo{" +

                "path=" + javaPath +

                ", version='" + version + '\'' +

                ", major=" + majorVersion +

                ", vendor='" + vendor + '\'' +

                ", jdk=" + jdk +

                '}';


    }



}