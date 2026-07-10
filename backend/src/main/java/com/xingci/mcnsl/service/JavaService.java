package com.xingci.mcnsl.service;


import com.xingci.mcnsl.manager.JavaManager;
import com.xingci.mcnsl.model.JavaInfo;

import org.springframework.stereotype.Service;


import java.util.List;



@Service
public class JavaService {


    private final JavaManager javaManager;



    public JavaService(
            JavaManager javaManager
    ){

        this.javaManager =
                javaManager;

    }





    public List<JavaInfo> getJavaList(){


        return javaManager.scan();


    }


}