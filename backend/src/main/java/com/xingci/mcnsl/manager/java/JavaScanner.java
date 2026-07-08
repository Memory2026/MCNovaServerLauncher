package com.xingci.mcnsl.manager.java;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Java 安装目录扫描器
 *
 * 仅负责寻找可能存在的 Java Home。
 */
@Component
public class JavaScanner {

    /**
     * 扫描系统所有可能的 Java Home
     */
    public Set<Path> scan() {

        Set<Path> result = new LinkedHashSet<>();

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            scanMacOS(result);
        } else if (os.contains("win")) {
            scanWindows(result);
        } else {
            scanLinux(result);
        }

        scanEnvironment(result);

        return result;
    }

    /**
     * macOS
     */
    private void scanMacOS(Set<Path> result) {

        scanDirectory(result,
                "/Library/Java/JavaVirtualMachines",
                true);

        scanDirectory(result,
                System.getProperty("user.home")
                        + "/Library/Java/JavaVirtualMachines",
                true);

        scanDirectory(result,
                "/opt/homebrew/Cellar",
                false);

        scanDirectory(result,
                "/usr/local/Cellar",
                false);

    }

    /**
     * Windows
     */
    private void scanWindows(Set<Path> result) {

        String[] roots = {

                "C:\\Program Files\\Java",

                "C:\\Program Files\\Eclipse Adoptium",

                "C:\\Program Files\\Microsoft",

                "C:\\Program Files\\Zulu",

                "C:\\Program Files\\BellSoft",

                "C:\\Program Files\\Amazon Corretto"

        };

        for (String root : roots) {

            scanDirectory(result,
                    root,
                    false);

        }

    }

    /**
     * Linux
     */
    private void scanLinux(Set<Path> result) {

        scanDirectory(result,
                "/usr/lib/jvm",
                false);

        scanDirectory(result,
                "/usr/java",
                false);

        scanDirectory(result,
                "/opt/java",
                false);

    }

    /**
     * 扫描 JAVA_HOME
     */
    private void scanEnvironment(Set<Path> result) {

        String javaHome = System.getenv("JAVA_HOME");

        if (javaHome != null && !javaHome.isBlank()) {

            Path path = Paths.get(javaHome);

            if (Files.exists(path)) {

                result.add(path);

            }

        }

    }

    /**
     * 扫描指定目录
     *
     * @param macBundle
     * 是否为 macOS 的 *.jdk 包
     */
    private void scanDirectory(Set<Path> result,
                               String root,
                               boolean macBundle) {

        File dir = new File(root);

        if (!dir.exists()) {

            return;

        }

        File[] children = dir.listFiles();

        if (children == null) {

            return;

        }

        for (File child : children) {

            if (!child.isDirectory()) {

                continue;

            }

            if (macBundle) {

                File home = new File(child,
                        "Contents/Home");

                if (home.exists()) {

                    result.add(home.toPath());

                }

            } else {

                result.add(child.toPath());

            }

        }

    }

}