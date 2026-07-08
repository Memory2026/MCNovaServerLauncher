package com.xingci.mcnsl.model;

import java.util.Objects;

/**
 * Java 环境信息
 */
public class JavaInfo {

    /**
     * 名称
     * 例如：Oracle JDK 21
     */
    private String name;

    /**
     * Java 版本
     */
    private String version;

    /**
     * Java Runtime Version
     */
    private String runtimeVersion;

    /**
     * Java 厂商
     */
    private String vendor;

    /**
     * 厂商版本
     */
    private String implementorVersion;

    /**
     * CPU 架构
     */
    private String architecture;

    /**
     * Java Modules
     */
    private String modules;

    /**
     * Java Home
     */
    private String path;

    /**
     * java 可执行文件
     */
    private String javaExecutable;

    /**
     * javac 可执行文件
     */
    private String javacExecutable;

    /**
     * 是否为 JDK
     */
    private boolean jdk;

    /**
     * 是否为当前运行 Java
     */
    private boolean current;

    /**
     * 是否默认
     */
    private boolean defaultJava;

    public JavaInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public void setRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getImplementorVersion() {
        return implementorVersion;
    }

    public void setImplementorVersion(String implementorVersion) {
        this.implementorVersion = implementorVersion;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getJavaExecutable() {
        return javaExecutable;
    }

    public void setJavaExecutable(String javaExecutable) {
        this.javaExecutable = javaExecutable;
    }

    public String getJavacExecutable() {
        return javacExecutable;
    }

    public void setJavacExecutable(String javacExecutable) {
        this.javacExecutable = javacExecutable;
    }

    public boolean isJdk() {
        return jdk;
    }

    public void setJdk(boolean jdk) {
        this.jdk = jdk;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isDefaultJava() {
        return defaultJava;
    }

    public void setDefaultJava(boolean defaultJava) {
        this.defaultJava = defaultJava;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaInfo)) return false;
        JavaInfo javaInfo = (JavaInfo) o;
        return Objects.equals(path, javaInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "JavaInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", vendor='" + vendor + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}