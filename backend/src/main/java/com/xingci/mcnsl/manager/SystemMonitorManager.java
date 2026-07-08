package com.xingci.mcnsl.manager;


import org.springframework.stereotype.Component;

import oshi.hardware.NetworkIF;


/**
 * 系统监控管理器
 *
 * 支持:
 * Windows
 * Linux
 * macOS
 *
 * 采集:
 * CPU
 * 内存
 * 磁盘
 * 网络速度
 */
@Component
public class SystemMonitorManager {


    /**
     * OSHI 系统信息
     *
     * 注意：
     * 不 import oshi.SystemInfo
     * 避免和 model.SystemInfo 冲突
     */
    private final oshi.SystemInfo oshiInfo =
            new oshi.SystemInfo();



    /**
     * CPU Tick
     */
    private long[] oldCpuTicks;



    /**
     * 网络历史数据
     */
    private long oldUploadBytes = 0;

    private long oldDownloadBytes = 0;


    private long lastNetworkTime =
            System.currentTimeMillis();




    public SystemMonitorManager() {


        oldCpuTicks =
                oshiInfo
                        .getHardware()
                        .getProcessor()
                        .getSystemCpuLoadTicks();

    }





    public com.xingci.mcnsl.model.SystemInfo getSystemInfo() {


        com.xingci.mcnsl.model.SystemInfo info =
                new com.xingci.mcnsl.model.SystemInfo();



        var hardware =
                oshiInfo.getHardware();



        /*
         =====================
             CPU
         =====================
         */


        var processor =
                hardware.getProcessor();



        double cpu =
                processor
                        .getSystemCpuLoadBetweenTicks(
                                oldCpuTicks
                        )
                        *
                        100;



        oldCpuTicks =
                processor.getSystemCpuLoadTicks();



        info.setCpuUsage(
                Math.round(cpu * 100)
                        /
                        100.0
        );





        /*
         =====================
             内存
         =====================
         */


        var memory =
                hardware.getMemory();



        long totalMemory =
                memory.getTotal();



        long availableMemory =
                memory.getAvailable();



        long usedMemory =
                totalMemory
                        -
                        availableMemory;



        info.setTotalMemory(
                totalMemory
        );


        info.setUsedMemory(
                usedMemory
        );



        info.setMemoryUsage(
                usedMemory
                        *
                        100.0
                        /
                        totalMemory
        );






        /*
         =====================
             磁盘
         =====================
         */


        long totalDisk = 0;

        long usedDisk = 0;



        var stores =
                oshiInfo
                        .getOperatingSystem()
                        .getFileSystem()
                        .getFileStores();



        for(var store : stores){


            long total =
                    store.getTotalSpace();



            if(total <= 0){
                continue;
            }



            String mount =
                    store.getMount();



            if(isSystemDisk(mount)){


                totalDisk =
                        total;


                usedDisk =
                        total
                                -
                                store.getUsableSpace();


                break;

            }

        }



        /*
         没找到系统盘
         使用第一个有效磁盘
         */

        if(totalDisk == 0){


            for(var store:stores){


                long total =
                        store.getTotalSpace();


                if(total > 0){


                    totalDisk =
                            total;


                    usedDisk =
                            total
                                    -
                                    store.getUsableSpace();


                    break;

                }

            }

        }



        info.setTotalDisk(
                totalDisk
        );


        info.setUsedDisk(
                usedDisk
        );



        if(totalDisk > 0){


            info.setDiskUsage(
                    usedDisk
                            *
                            100.0
                            /
                            totalDisk
            );

        }





        /*
         =====================
             网络速度
         =====================
         */


        long uploadBytes = 0;

        long downloadBytes = 0;



        for(NetworkIF net :
                hardware.getNetworkIFs()){


            /*
             过滤虚拟网卡
             */

            String name =
                    net.getName()
                            .toLowerCase();



            if(
                    name.contains("lo")
                            ||
                            name.contains("docker")
                            ||
                            name.contains("vmnet")
                            ||
                            name.contains("vbox")
            ){

                continue;

            }



            net.updateAttributes();



            uploadBytes +=
                    net.getBytesSent();



            downloadBytes +=
                    net.getBytesRecv();

        }




        long now =
                System.currentTimeMillis();



        long deltaTime =
                now
                        -
                        lastNetworkTime;



        if(deltaTime > 0){



            long uploadSpeed =
                    (
                            uploadBytes
                                    -
                                    oldUploadBytes
                    )
                            *
                            1000
                            /
                            deltaTime;



            long downloadSpeed =
                    (
                            downloadBytes
                                    -
                                    oldDownloadBytes
                    )
                            *
                            1000
                            /
                            deltaTime;



            info.setUploadSpeed(
                    Math.max(
                            uploadSpeed,
                            0
                    )
            );



            info.setDownloadSpeed(
                    Math.max(
                            downloadSpeed,
                            0
                    )
            );


        }



        oldUploadBytes =
                uploadBytes;


        oldDownloadBytes =
                downloadBytes;


        lastNetworkTime =
                now;




        return info;

    }





    /**
     * 判断系统盘
     */
    private boolean isSystemDisk(
            String mount
    ){


        if(mount == null){
            return false;
        }



        String os =
                System
                        .getProperty("os.name")
                        .toLowerCase();



        /*
         Windows:
         C:
         */

        if(os.contains("win")){


            return mount.matches(
                    "[A-Za-z]:\\\\?"
            );

        }



        /*
         Linux/macOS:
         /
         */

        return "/".equals(mount);

    }

}