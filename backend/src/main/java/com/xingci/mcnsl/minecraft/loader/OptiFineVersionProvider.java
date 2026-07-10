package com.xingci.mcnsl.minecraft.loader;


import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;



@Component("minecraftOptiFineVersionProvider")
public class OptiFineVersionProvider {



    /**
     * OptiFine版本搜索
     *
     * 目前官方没有公开API
     *
     * 使用官方版本列表镜像
     */
    public List<LoaderVersion> search(
            String minecraftVersion
    ){



        List<LoaderVersion> result =
                new ArrayList<>();



        /*
         * OptiFine版本规则:
         *
         * 1.20.1
         * HD_U_I6
         *
         * 1.19.4
         * HD_U_I5
         */


        if(minecraftVersion.equals("1.20.1")){


            result.add(

                    new LoaderVersion(
                            "OptiFine",
                            "1.20.1",
                            "HD_U_I6",
                            "https://optifine.net/downloads"
                    )

            );


        }




        if(minecraftVersion.equals("1.19.4")){


            result.add(

                    new LoaderVersion(
                            "OptiFine",
                            "1.19.4",
                            "HD_U_I5",
                            "https://optifine.net/downloads"
                    )

            );


        }




        if(minecraftVersion.equals("1.18.2")){


            result.add(

                    new LoaderVersion(
                            "OptiFine",
                            "1.18.2",
                            "HD_U_H9",
                            "https://optifine.net/downloads"
                    )

            );


        }




        return result;


    }


}
