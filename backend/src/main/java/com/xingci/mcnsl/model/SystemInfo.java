package com.xingci.mcnsl.model;


public class SystemInfo {


    private double cpuUsage;


    private long totalMemory;

    private long usedMemory;

    private double memoryUsage;


    private long totalDisk;

    private long usedDisk;

    private double diskUsage;


    private long uploadSpeed;

    private long downloadSpeed;



    public double getCpuUsage() {
        return cpuUsage;
    }


    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }



    public long getTotalMemory() {
        return totalMemory;
    }


    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }



    public long getUsedMemory() {
        return usedMemory;
    }


    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }



    public double getMemoryUsage() {
        return memoryUsage;
    }


    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }



    public long getTotalDisk() {
        return totalDisk;
    }


    public void setTotalDisk(long totalDisk) {
        this.totalDisk = totalDisk;
    }



    public long getUsedDisk() {
        return usedDisk;
    }


    public void setUsedDisk(long usedDisk) {
        this.usedDisk = usedDisk;
    }



    public double getDiskUsage() {
        return diskUsage;
    }


    public void setDiskUsage(double diskUsage) {
        this.diskUsage = diskUsage;
    }



    public long getUploadSpeed() {
        return uploadSpeed;
    }


    public void setUploadSpeed(long uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }



    public long getDownloadSpeed() {
        return downloadSpeed;
    }


    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }
}