package com.xingci.mcnsl.api.minecraft;


import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Component
public class NeoForgeApi {



    /**
     * 获取 NeoForge 版本
     */
    public List<String> getVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {


        return new ArrayList<>();

    }


}