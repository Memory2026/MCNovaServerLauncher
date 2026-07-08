package com.xingci.mcnsl.manager.java;

import com.xingci.mcnsl.model.JavaInfo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 解析 JDK/JRE 的 release 文件
 *
 * Java Home
 * ├── bin
 * ├── lib
 * ├── conf
 * └── release   ← 本文件负责解析这里
 */
@Component
public class ReleaseParser {

    /**
     * 解析 Java Home
     *
     * @param javaHome Java Home
     * @return JavaInfo
     */
    public JavaInfo parse(Path javaHome) {

        JavaInfo info = new JavaInfo();

        info.setPath(javaHome.toString());

        Path releaseFile = javaHome.resolve("release");

        if (!Files.exists(releaseFile)) {

            info.setName("Unknown Java");
            info.setVendor("Unknown");
            info.setVersion("Unknown");

            return info;
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(releaseFile)) {

            properties.load(input);

        } catch (IOException e) {

            info.setName("Unknown Java");
            info.setVendor("Unknown");
            info.setVersion("Unknown");

            return info;
        }

        String version =
                trim(properties.getProperty("JAVA_VERSION"));

        String runtimeVersion =
                trim(properties.getProperty("JAVA_RUNTIME_VERSION"));

        String implementor =
                trim(properties.getProperty("IMPLEMENTOR"));

        String implementorVersion =
                trim(properties.getProperty("IMPLEMENTOR_VERSION"));

        String osArch =
                trim(properties.getProperty("OS_ARCH"));

        String modules =
                trim(properties.getProperty("MODULES"));

        info.setVersion(version);
        info.setVendor(implementor);

        // Java 名称
        info.setName(buildName(implementor, version));

        // 如果 JavaInfo 中还没有这些字段，
        // 下一步我们会一起补充。
        try {
            info.getClass()
                    .getMethod("setRuntimeVersion", String.class)
                    .invoke(info, runtimeVersion);

            info.getClass()
                    .getMethod("setArchitecture", String.class)
                    .invoke(info, osArch);

            info.getClass()
                    .getMethod("setModules", String.class)
                    .invoke(info, modules);

            info.getClass()
                    .getMethod("setImplementorVersion", String.class)
                    .invoke(info, implementorVersion);

        } catch (Exception ignored) {
            // 如果 JavaInfo 暂时没有这些字段，不影响运行
        }

        return info;

    }

    /**
     * 去掉 release 文件中的双引号
     */
    private String trim(String value) {

        if (value == null) {
            return "";
        }

        value = value.trim();

        if (value.startsWith("\"")) {
            value = value.substring(1);
        }

        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;

    }

    /**
     * 根据厂商生成名称
     */
    private String buildName(String vendor,
                             String version) {

        if (vendor == null || vendor.isBlank()) {

            return "Java " + version;

        }

        if (vendor.contains("Oracle")) {

            return "Oracle JDK " + version;

        }

        if (vendor.contains("Eclipse")) {

            return "Temurin JDK " + version;

        }

        if (vendor.contains("Microsoft")) {

            return "Microsoft JDK " + version;

        }

        if (vendor.contains("Azul")) {

            return "Zulu JDK " + version;

        }

        if (vendor.contains("BellSoft")) {

            return "Liberica JDK " + version;

        }

        if (vendor.contains("Amazon")) {

            return "Corretto JDK " + version;

        }

        return vendor + " JDK " + version;

    }

}