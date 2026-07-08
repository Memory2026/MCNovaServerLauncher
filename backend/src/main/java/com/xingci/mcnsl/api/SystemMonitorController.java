package com.xingci.mcnsl.api;


import com.xingci.mcnsl.manager.SystemMonitorManager;
import com.xingci.mcnsl.model.SystemInfo;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/system")
@CrossOrigin("*")
public class SystemMonitorController {


    private final SystemMonitorManager manager;


    public SystemMonitorController(
            SystemMonitorManager manager
    ){

        this.manager=manager;

    }



    @GetMapping("/info")
    public SystemInfo info(){

        return manager.getSystemInfo();

    }

}