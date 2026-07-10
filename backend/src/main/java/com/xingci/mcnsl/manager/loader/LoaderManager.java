package com.xingci.mcnsl.manager.loader;


import com.xingci.mcnsl.model.LoaderVersionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class LoaderManager {


    private final ForgeVersionProvider forgeVersionProvider;

    private final NeoForgeVersionProvider neoForgeVersionProvider;

    private final FabricVersionProvider fabricVersionProvider;

    private final OptiFineVersionProvider optiFineVersionProvider;



    public List<LoaderVersionInfo> getVersions(
            String loader,
            String minecraftVersion
    ){

        return switch (loader.toLowerCase()) {


            case "forge" ->
                    forgeVersionProvider.findVersions(minecraftVersion);


            case "neoforge" ->
                    neoForgeVersionProvider.findVersions(minecraftVersion);


            case "fabric" ->
                    fabricVersionProvider.findVersions(minecraftVersion);


            case "optifine" ->
                    optiFineVersionProvider.findVersions(minecraftVersion);


            default ->
                    List.of();

        };

    }

}