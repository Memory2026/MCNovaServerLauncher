package com.xingci.mcnsl.manager;

import com.xingci.mcnsl.manager.java.JavaScanner;
import com.xingci.mcnsl.manager.java.JavaValidator;
import com.xingci.mcnsl.model.JavaInfo;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class JavaManager {


    private final JavaScanner scanner;

    private final JavaValidator validator;


    public JavaManager(
            JavaScanner scanner,
            JavaValidator validator
    ) {

        this.scanner = scanner;
        this.validator = validator;

    }



    public List<JavaInfo> getJavaList() {


        List<JavaInfo> result =
                new ArrayList<>();


        Set<Path> paths =
                scanner.scan();



        for(Path path : paths){


            JavaInfo info =
                    validator.validate(path);


            if(info != null){

                result.add(info);

            }

        }


        return result;

    }


}