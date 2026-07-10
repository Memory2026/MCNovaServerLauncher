package com.xingci.mcnsl.api;


import com.xingci.mcnsl.manager.JavaManager;
import com.xingci.mcnsl.manager.java.JavaValidator;
import com.xingci.mcnsl.model.JavaInfo;


import org.springframework.web.bind.annotation.*;


import java.nio.file.Path;
import java.util.List;
import java.util.Map;


/**
 * Java管理控制器
 *
 * 提供Java环境接口
 */
@RestController
@RequestMapping("/api/java")
@CrossOrigin
public class JavaController {


    private final JavaManager javaManager;


    private final JavaValidator javaValidator;





    public JavaController(
            JavaManager javaManager,
            JavaValidator javaValidator
    ){

        this.javaManager =
                javaManager;


        this.javaValidator =
                javaValidator;

    }









    /**
     * 扫描Java
     *
     * GET:
     *
     * /api/java/scan
     */
    @GetMapping("/scan")
    public List<JavaInfo> scan(){


        return javaManager
                .scan();


    }









    /**
     * 获取所有Java
     */
    @GetMapping("/list")
    public List<JavaInfo> list(){


        return javaManager
                .getAll();


    }









    /**
     * 获取Minecraft推荐Java
     *
     * 例如:
     *
     * 1.21 -> Java21
     */
    @GetMapping("/select")
    public JavaInfo select(
            @RequestParam String version
    ){


        return javaManager
                .select(
                        version
                );


    }









    /**
     * 检测Java目录
     *
     * POST:
     *
     * /api/java/validate
     *
     * body:
     *
     * {
     *   "path":"C:/Java/jdk21"
     * }
     */
    @PostMapping("/validate")
    public Map<String,Object> validate(
            @RequestBody Map<String,String> body
    ){



        Path path =
                Path.of(
                        body.get(
                                "path"
                        )
                );





        boolean valid =
                javaValidator
                        .validate(
                                path
                        );





        if(!valid){


            return Map.of(

                    "valid",
                    false

            );


        }







        JavaInfo info =
                javaValidator
                        .inspect(
                                path
                        );





        return Map.of(

                "valid",
                true,

                "java",
                info

        );


    }









    /**
     * 获取Java版本
     */
    @GetMapping("/version")
    public String version(
            @RequestParam String path
    )
            throws Exception {


        return javaValidator
                .getVersion(

                        Path.of(
                                path
                        )

                );


    }


    /**
     * 自动检测系统Java
     */
    @PostMapping("/detect")
    public List<JavaInfo> detect(){

        return javaManager
                .scan();

    }


    /**
     * 设置默认Java
     */
    @PostMapping("/default")
    public JavaInfo setDefault(
            @RequestBody Map<String,String> body
    ){

        String path = body.get("path");

        return javaManager
                .setDefault(
                        path
                );

    }


    /**
     * 获取默认Java
     */
    @GetMapping("/default")
    public JavaInfo getDefault(){

        return javaManager
                .getDefault();

    }


    /**
     * 获取推荐Java版本（根据Minecraft版本）
     */
    @GetMapping("/recommend")
    public Map<String,Object> recommend(
            @RequestParam(required = false) String minecraftVersion
    ){

        List<JavaInfo> all = javaManager.getAll();

        if(all.isEmpty()){

            return Map.of(
                    "success",
                    false,
                    "message",
                    "未检测到Java环境"
            );

        }

        String mcVersion = minecraftVersion != null ? minecraftVersion : "1.21";

        JavaInfo recommended = javaManager.select(mcVersion);

        JavaInfo currentDefault = javaManager.getDefault();

        return Map.of(
                "success",
                true,
                "recommended",
                recommended,
                "minecraftVersion",
                mcVersion,
                "requiredJava",
                getRequiredJavaVersion(mcVersion),
                "currentDefault",
                currentDefault,
                "all",
                all
        );

    }


    /**
     * 获取Minecraft所需Java版本
     */
    private int getRequiredJavaVersion(String version){

        try{

            String[] split = version.split("\\.");

            int major = Integer.parseInt(split[1]);

            int minor = split.length>2 ? Integer.parseInt(split[2]) : 0;

            if(major==20 && minor>=5){

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


}