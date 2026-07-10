package com.xingci.mcnsl.minecraft.loader;



import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;



@Component("minecraftLoaderManager")
public class LoaderManager {



    private final NeoForgeVersionProvider neoForge;

    private final OptiFineVersionProvider optifine;




    public LoaderManager(
            NeoForgeVersionProvider neoForge,
            OptiFineVersionProvider optifine
    ){

        this.neoForge = neoForge;

        this.optifine = optifine;

    }





    /**
     * 搜索全部加载器
     */
    public List<LoaderVersion> search(
            String loader,
            String minecraftVersion
    ){



        return switch(loader.toLowerCase()){



            case "neoforge" ->

                    neoForge.search(
                            minecraftVersion
                    );



            case "optifine" ->

                    optifine.search(
                            minecraftVersion
                    );



            default ->

                    new ArrayList<>();

        };


    }




    /**
     * 搜索全部
     */
    public List<LoaderVersion> searchAll(
            String minecraftVersion
    ){


        List<LoaderVersion> list =
                new ArrayList<>();


        list.addAll(
                neoForge.search(
                        minecraftVersion
                )
        );


        list.addAll(
                optifine.search(
                        minecraftVersion
                )
        );



        return list;


    }



}
