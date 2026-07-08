package com.xingci.mcnsl.manager;

import com.xingci.mcnsl.model.JavaInfo;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class JavaManager {

    public List<JavaInfo> getJavaList() {
        List<JavaInfo> list = new ArrayList<>();

        try {
            // 1. 尝试执行你原本项目底层的原生扫描（如果没有，保持空即可）
            // List<JavaInfo> scannedList = ...
            // if (scannedList != null) list.addAll(scannedList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 🚀【全新多版本强力检索】
        // 策略 A：去扫描 Mac 最标准的官方多版本安装目录
        File macJavaDir = new File("/Library/Java/JavaVirtualMachines");
        if (macJavaDir.exists() && macJavaDir.isDirectory()) {
            File[] jdks = macJavaDir.listFiles();
            if (jdks != null && jdks.length > 0) {
                for (File jdk : jdks) {
                    // Mac 标准 JDK 可执行文件路径
                    String targetPath = jdk.getAbsolutePath() + "/Contents/Home/bin/java";
                    File javaFile = new File(targetPath);

                    if (javaFile.exists()) {
                        // 检查是否已经添加过这个路径，防止重复
                        boolean isDuplicate = false;
                        for (JavaInfo existing : list) {
                            if (existing.getPath().equals(javaFile.getAbsolutePath())) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            JavaInfo info = new JavaInfo();
                            String folderName = jdk.getName(); // 例如 "jdk-21.jdk" 或 "jdk-26.jdk"
                            info.setName(folderName);
                            info.setPath(javaFile.getAbsolutePath());

                            // 动态提取文件夹名字里的数字作为版本号展示
                            String parsedVersion = folderName.replaceAll("[^0-9]", "");
                            info.setVersion(parsedVersion.isEmpty() ? "未知" : parsedVersion);

                            // 这里可以根据情况给第一个设置为默认，或者全部设为 false 等待前端切换
                            info.setCurrent(list.isEmpty());

                            list.add(info);
                            // 📢 注意：这里【去掉了 break】，让它把所有文件夹遍历个遍！
                        }
                    }
                }
            }
        }

        // 策略 B：作为补充，如果上面没捞全，把当前运行时的 JAVA_HOME 也补进去
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null || javaHome.isEmpty()) {
            javaHome = System.getProperty("java.home");
        }
        if (javaHome != null && !javaHome.isEmpty()) {
            String javaExePath = javaHome + File.separator + "bin" + File.separator + "java";
            File javaExeFile = new File(javaExePath);

            if (javaExeFile.exists()) {
                // 判断这个默认路径是否已经在上面的列表里了
                boolean isDuplicate = false;
                for (JavaInfo existing : list) {
                    if (existing.getPath().equals(javaExeFile.getAbsolutePath())) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    JavaInfo info = new JavaInfo();
                    info.setName("系统运行环境 Java");
                    info.setPath(javaExeFile.getAbsolutePath());
                    String sysVersion = System.getProperty("java.version");
                    info.setVersion((sysVersion != null) ? sysVersion.split("\\.")[0] : "默认");
                    info.setCurrent(list.isEmpty());
                    list.add(info);
                }
            }
        }

        return list;
    }
}