package com.xingci.mcnsl.manager.minecraft;


import com.xingci.mcnsl.api.minecraft.FabricApi;
import com.xingci.mcnsl.api.minecraft.ForgeApi;
import com.xingci.mcnsl.api.minecraft.MojangAPI;
import com.xingci.mcnsl.api.minecraft.NeoForgeApi;
import com.xingci.mcnsl.api.minecraft.QuiltApi;


import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.List;



/**
 * Minecraft版本管理器
 *
 * 管理:
 *
 * Vanilla
 * Fabric
 * Forge
 * NeoForge
 * Quilt
 */
@Component
public class VersionManager {


    private final MojangAPI mojangAPI;

    private final FabricApi fabricApi;

    private final ForgeApi forgeApi;

    private final NeoForgeApi neoForgeApi;

    private final QuiltApi quiltApi;



    public VersionManager(
            MojangAPI mojangAPI,
            FabricApi fabricApi,
            ForgeApi forgeApi,
            NeoForgeApi neoForgeApi,
            QuiltApi quiltApi
    ){

        this.mojangAPI = mojangAPI;

        this.fabricApi = fabricApi;

        this.forgeApi = forgeApi;

        this.neoForgeApi = neoForgeApi;

        this.quiltApi = quiltApi;

    }





    /**
     * Minecraft官方版本
     */
    public List<String> getMinecraftVersions()
            throws IOException, InterruptedException {


        return mojangAPI.getVersions();


    }





    /**
     * Fabric版本
     */
    public List<String> getFabricVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {


        return fabricApi.getVersions(
                minecraftVersion
        );


    }





    /**
     * Forge版本
     */
    public List<String> getForgeVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {


        return forgeApi.getVersions(
                minecraftVersion
        );


    }





    /**
     * NeoForge版本
     */
    public List<String> getNeoForgeVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {


        return neoForgeApi.getVersions(
                minecraftVersion
        );


    }





    /**
     * Quilt版本
     */
    public List<String> getQuiltVersions(
            String minecraftVersion
    )
            throws IOException, InterruptedException {


        return quiltApi.getVersions(
                minecraftVersion
        );


    }


}