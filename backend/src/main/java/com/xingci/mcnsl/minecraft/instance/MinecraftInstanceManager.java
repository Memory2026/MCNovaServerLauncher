package com.xingci.mcnsl.minecraft.instance;


import org.springframework.stereotype.Component;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Minecraft实例管理器
 *
 * 负责:
 *
 * 创建实例
 * 保存实例
 * 查询实例
 * 删除实例
 */
@Component
public class MinecraftInstanceManager {



    /**
     * 实例缓存
     */
    private final Map<String, MinecraftInstance> instances =

            new ConcurrentHashMap<>();









    /**
     * 创建实例
     *
     * String路径版本
     *
     * Controller使用
     */
    public MinecraftInstance create(
            String name,
            String version,
            String path
    )
            throws Exception {



        return create(

                name,

                version,

                "vanilla",

                Path.of(path)

        );


    }









    /**
     * 创建实例
     *
     * Path版本
     */
    public MinecraftInstance create(
            String name,
            String version,
            Path path
    )
            throws Exception {



        return create(

                name,

                version,

                "vanilla",

                path

        );


    }









    /**
     * 创建实例
     *
     * 完整版本
     */
    public MinecraftInstance create(
            String name,
            String version,
            String loader,
            Path path
    )
            throws Exception {



        if(path != null){


            Files.createDirectories(

                    path

            );


        }






        MinecraftInstance instance =

                new MinecraftInstance();








        instance.setId(

                UUID.randomUUID()

                        .toString()

        );








        instance.setName(

                name

        );








        instance.setVersion(

                version

        );








        instance.setLoader(

                loader

        );








        instance.setPath(

                path.toString()

        );








        return save(

                instance

        );


    }









    /**
     * 保存实例
     */
    public MinecraftInstance save(
            MinecraftInstance instance
    ){


        instances.put(

                instance.getId(),

                instance

        );


        return instance;


    }









    /**
     * 获取实例
     */
    public MinecraftInstance get(
            String id
    ){


        return instances.get(

                id

        );


    }









    /**
     * 获取全部实例
     */
    public List<MinecraftInstance> list(){


        return new ArrayList<>(

                instances.values()

        );


    }









    /**
     * 删除实例
     */
    public boolean delete(
            String id
    ){


        return instances.remove(

                id

        ) != null;


    }









    /**
     * 兼容旧代码
     */
    public boolean remove(
            String id
    ){


        return delete(

                id

        );


    }









    /**
     * 判断实例是否存在
     */
    public boolean exists(
            String id
    ){


        return instances.containsKey(

                id

        );


    }



}