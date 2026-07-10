package com.xingci.mcnsl.minecraft.loader;


import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController("minecraftLoaderController")
@RequestMapping("/api/minecraft/loaders")
public class LoaderController {



    private final LoaderManager manager;



    public LoaderController(
            LoaderManager manager
    ){

        this.manager = manager;

    }





    @GetMapping("/{loader}")
    public List<LoaderVersion> search(
            @PathVariable String loader,
            @RequestParam String mc
    ){


        return manager.search(
                loader,
                mc
        );


    }



}
