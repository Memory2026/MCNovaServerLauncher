package com.xingci.mcnsl.manager.loader;



import org.springframework.stereotype.Component;

import java.util.List;



@Component
public class LoaderMatcher {


    public List<String> getLoaderVersions(
            String loader,
            String mcVersion
    ){


        return switch(loader.toLowerCase()){


            case "forge" ->
                    CompatibilityData.FORGE
                            .getOrDefault(
                                    mcVersion,
                                    List.of()
                            );


            case "neoforge" ->
                    CompatibilityData.NEOFORGE
                            .getOrDefault(
                                    mcVersion,
                                    List.of()
                            );



            case "fabric" ->
                    CompatibilityData.FABRIC
                            .getOrDefault(
                                    mcVersion,
                                    List.of()
                            );


            default ->
                    List.of();

        };

    }

}