package com.xingci.mcnsl.manager.loader;


import com.xingci.mcnsl.model.LoaderVersionInfo;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;



@Component
public class OptiFineVersionProvider {


    public List<LoaderVersionInfo> findVersions(
            String mcVersion
    ){


        List<LoaderVersionInfo> list =
                new ArrayList<>();


        /*
         后续接入 OptiFine 官网解析
         这里先预留
        */


        return list;

    }

}