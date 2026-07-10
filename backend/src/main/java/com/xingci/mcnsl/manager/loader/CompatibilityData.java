package com.xingci.mcnsl.manager.loader;


import java.util.*;


public class CompatibilityData {


    /**
     * Forge支持表
     */
    public static final Map<String,List<String>> FORGE =
            new HashMap<>();


    /**
     * NeoForge支持表
     */
    public static final Map<String,List<String>> NEOFORGE =
            new HashMap<>();


    /**
     * Fabric Loader支持表
     */
    public static final Map<String,List<String>> FABRIC =
            new HashMap<>();



    static {


        /*
         Forge
         */


        FORGE.put(
                "1.20.1",
                List.of(
                        "47.3.0",
                        "47.2.20",
                        "47.2.0"
                )
        );


        FORGE.put(
                "1.19.2",
                List.of(
                        "43.4.0",
                        "43.3.5"
                )
        );



        FORGE.put(
                "1.12.2",
                List.of(
                        "14.23.5.2860"
                )
        );



        /*
          NeoForge
         */


        NEOFORGE.put(
                "1.20.1",
                List.of(
                        "47.1.106"
                )
        );


        NEOFORGE.put(
                "1.21",
                List.of(
                        "21.0.167"
                )
        );



        /*
          Fabric
         */


        FABRIC.put(
                "1.21",
                List.of(
                        "0.16.14",
                        "0.16.10"
                )
        );


        FABRIC.put(
                "1.20.1",
                List.of(
                        "0.15.11",
                        "0.15.10"
                )
        );

    }



}