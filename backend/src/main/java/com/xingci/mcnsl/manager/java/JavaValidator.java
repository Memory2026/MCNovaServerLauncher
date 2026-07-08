package com.xingci.mcnsl.manager.java;

import com.xingci.mcnsl.model.JavaInfo;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 环境验证器
 *
 * 负责：
 * 1. 判断目录是否为 Java Home
 * 2. 判断 JDK / JRE
 * 3. 获取 java、javac 可执行文件
 */
@Component
public class JavaValidator {

    /**
     * 验证 Java Home
     *
     * @param javaHome Java Home
     * @return JavaInfo，不合法返回 null
     */
    public JavaInfo validate(Path javaHome) {

        if (javaHome == null) {
            return null;
        }

        if (!Files.exists(javaHome)) {
            return null;
        }

        if (!Files.isDirectory(javaHome)) {
            return null;
        }

        boolean windows =
                System.getProperty("os.name")
                        .toLowerCase()
                        .contains("win");

        Path javaExecutable =
                javaHome.resolve("bin")
                        .resolve(windows ? "java.exe" : "java");

        if (!Files.exists(javaExecutable)) {
            return null;
        }

        Path release =
                javaHome.resolve("release");

        if (!Files.exists(release)) {
            return null;
        }

        Path javacExecutable =
                javaHome.resolve("bin")
                        .resolve(windows ? "javac.exe" : "javac");

        JavaInfo info = new JavaInfo();

        info.setPath(javaHome.toString());

        info.setJavaExecutable(
                javaExecutable.toAbsolutePath().toString());

        if (Files.exists(javacExecutable)) {

            info.setJavacExecutable(
                    javacExecutable.toAbsolutePath().toString());

            info.setJdk(true);

        } else {

            info.setJdk(false);

        }

        String currentHome =
                System.getProperty("java.home");

        if (currentHome != null &&
                javaHome.toAbsolutePath()
                        .normalize()
                        .equals(Path.of(currentHome)
                                .toAbsolutePath()
                                .normalize())) {

            info.setCurrent(true);

        }

        return info;
    }

}