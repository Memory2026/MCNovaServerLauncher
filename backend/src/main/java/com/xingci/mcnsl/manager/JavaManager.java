package com.xingci.mcnsl.manager;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xingci.mcnsl.manager.java.JavaScanner;
import com.xingci.mcnsl.manager.java.JavaValidator;
import com.xingci.mcnsl.model.JavaInfo;


/**
 * Java管理器
 *
 * 负责：
 *
 * 1. 管理本机Java
 * 2. 选择Minecraft适配Java
 * 3. 管理默认Java
 */
@Service
public class JavaManager {


    private final JavaScanner javaScanner;


    private final JavaValidator javaValidator;


    private List<JavaInfo> cache;


    private JavaInfo defaultJava;


    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".mcnova", "java-config.json");


    public JavaManager(
            JavaScanner javaScanner,
            JavaValidator javaValidator
    ){

        this.javaScanner =
                javaScanner;


        this.javaValidator =
                javaValidator;

        loadDefaultJava();

    }


    private void loadDefaultJava(){

        if(Files.exists(CONFIG_PATH)){

            try{

                String content = Files.readString(CONFIG_PATH);

                if(content.contains("\"defaultPath\":\"")){

                    int start = content.indexOf("\"defaultPath\":\"") + 14;

                    int end = content.indexOf("\"", start);

                    String path = content.substring(start, end);

                    Path javaPath = Path.of(path);

                    if(Files.exists(javaPath)){

                        defaultJava = javaValidator.inspect(javaPath.getParent().getParent());

                    }

                }

            }
            catch(Exception ignored){

            }

        }

        if(defaultJava == null){

            List<JavaInfo> all = scan();

            if(!all.isEmpty()){

                defaultJava = all.get(0);

            }

        }

    }


    private void saveDefaultJava(){

        try{

            Files.createDirectories(CONFIG_PATH.getParent());

            String json = "{\"defaultPath\":\"" + defaultJava.getJavaPath().toString() + "\"}";

            Files.writeString(CONFIG_PATH, json);

        }
        catch(IOException ignored){

        }

    }









    /**
     * 扫描Java环境
     */
    public List<JavaInfo> scan(){


        cache =
                javaScanner.scan();


        return cache;


    }









    /**
     * 获取全部Java
     */
    public List<JavaInfo> getAll(){


        if(cache == null){


            return scan();


        }


        return cache;


    }









    /**
     * 自动选择Java
     *
     * Minecraft规则：
     *
     * <=1.16
     * Java 8
     *
     * 1.17-1.20.4
     * Java 17
     *
     * >=1.20.5
     * Java 21
     */
    public JavaInfo select(
            String minecraftVersion
    ){


        int required =
                getRequiredVersion(
                        minecraftVersion
                );





        return getAll()
                .stream()

                .filter(
                        java ->
                                java.getMajorVersion()
                                        >=
                                        required
                )

                .min(

                        Comparator.comparingInt(
                                JavaInfo::getMajorVersion
                        )

                )

                .orElseThrow(

                        () ->
                                new IllegalStateException(

                                        "没有找到Java "
                                                +
                                                required

                                                +
                                                " 或更高版本"

                                )

                );


    }









    /**
     * 根据Minecraft版本判断Java
     */
    private int getRequiredVersion(
            String version
    ){


        try{


            String[] split =
                    version.split("\\.");



            int major =
                    Integer.parseInt(
                            split[1]
                    );



            int minor =
                    split.length>2
                            ?
                            Integer.parseInt(split[2])
                            :
                            0;




            if(major==20
                    &&
                    minor>=5){

                return 21;

            }



            if(major>=21){

                return 21;

            }




            if(major>=17){

                return 17;

            }


            return 8;


        }
        catch(Exception e){


            return 17;


        }


    }









    /**
     * 获取Java路径
     */
    public Path getJavaPath(
            String version
    ){


        return select(version)
                .getJavaPath();


    }


    /**
     * 设置默认Java
     */
    public JavaInfo setDefault(String path){

        Path javaPath = Path.of(path);

        if(Files.exists(javaPath)){

            defaultJava = javaValidator.inspect(javaPath.getParent().getParent());

            saveDefaultJava();

            return defaultJava;

        }

        List<JavaInfo> all = getAll();

        Optional<JavaInfo> found = all.stream()
                .filter(j -> j.getJavaPath().toString().contains(path) ||
                            path.contains(j.getJavaPath().toString()))
                .findFirst();

        if(found.isPresent()){

            defaultJava = found.get();

            saveDefaultJava();

            return defaultJava;

        }

        throw new IllegalArgumentException("未找到指定的Java环境: " + path);

    }


    /**
     * 获取默认Java
     */
    public JavaInfo getDefault(){

        if(defaultJava == null){

            List<JavaInfo> all = getAll();

            if(!all.isEmpty()){

                defaultJava = all.get(0);

            }

        }

        return defaultJava;

    }


    /**
     * 获取所有Java（包含默认标记）
     */
    public List<JavaInfo> getAllWithDefault(){

        List<JavaInfo> all = getAll();

        return all;

    }


}