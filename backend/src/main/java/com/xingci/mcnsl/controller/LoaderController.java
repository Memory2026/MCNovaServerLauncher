package com.xingci.mcnsl.controller;


import com.xingci.mcnsl.manager.loader.LoaderManager;
import com.xingci.mcnsl.model.LoaderVersionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController("downloadLoaderController")
@RequestMapping("/api/loaders")
@RequiredArgsConstructor
public class LoaderController {


    private final LoaderManager loaderManager;



    @GetMapping("/{loader}/versions")
    public List<LoaderVersionInfo> versions(
            @PathVariable String loader,
            @RequestParam String mcVersion
    ){

        return loaderManager.getVersions(
                loader,
                mcVersion
        );

    }

}
