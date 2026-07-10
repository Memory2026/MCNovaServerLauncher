package com.xingci.mcnsl.api.minecraft;


import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Component
public class ForgeApi {



    /**
     * 获取 Forge 版本
     *
     * TODO:
     * 接入 Forge Maven API
     */
    public List<String> getVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {


        return new ArrayList<>();

    }


}