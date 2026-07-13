package com.xingci.mcnsl.minecraft.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaUtils {

    public static class JavaInfo {
        private String version;
        private Path javaExecutable;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Path getJavaExecutable() {
            return javaExecutable;
        }

        public void setJavaExecutable(Path javaExecutable) {
            this.javaExecutable = javaExecutable;
        }
    }

    public static JavaInfo findJavaForMinecraftVersion(String minecraftVersion) {
        List<String> javaPaths = List.of(
                "/usr/bin/java",
                "/usr/local/bin/java",
                "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java",
                "/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java",
                "/Library/Java/JavaVirtualMachines/jdk-16.jdk/Contents/Home/bin/java",
                "/Library/Java/JavaVirtualMachines/jdk-8.jdk/Contents/Home/bin/java",
                System.getenv("JAVA_HOME") != null ? System.getenv("JAVA_HOME") + "/bin/java" : ""
        );

        int requiredJavaVersion = getRequiredJavaVersion(minecraftVersion);

        for (String javaPath : javaPaths) {
            if (javaPath.isEmpty()) continue;
            Path path = Paths.get(javaPath);
            if (path.toFile().exists()) {
                String version = getJavaVersion(javaPath);
                if (version != null) {
                    int majorVersion = parseJavaMajorVersion(version);
                    if (majorVersion >= requiredJavaVersion) {
                        JavaInfo info = new JavaInfo();
                        info.setVersion(version);
                        info.setJavaExecutable(path);
                        return info;
                    }
                }
            }
        }

        return null;
    }

    private static int getRequiredJavaVersion(String minecraftVersion) {
        if (minecraftVersion.startsWith("1.20") || minecraftVersion.startsWith("1.21")) {
            return 17;
        } else if (minecraftVersion.startsWith("1.18") || minecraftVersion.startsWith("1.19")) {
            return 17;
        } else if (minecraftVersion.startsWith("1.17")) {
            return 16;
        } else {
            return 8;
        }
    }

    private static String getJavaVersion(String javaPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(javaPath, "-version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
            String output = scanner.hasNext() ? scanner.next() : "";
            process.waitFor();

            Pattern pattern = Pattern.compile("version \"([^\"]+)\"");
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int parseJavaMajorVersion(String version) {
        try {
            if (version.startsWith("1.")) {
                return Integer.parseInt(version.substring(2, 3));
            }
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 0;
        }
    }

}