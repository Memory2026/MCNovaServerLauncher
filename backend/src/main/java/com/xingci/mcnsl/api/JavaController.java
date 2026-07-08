package com.xingci.mcnsl.api;

import com.xingci.mcnsl.model.ApiResponse;
import com.xingci.mcnsl.model.JavaInfo;
import com.xingci.mcnsl.service.JavaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin; // 1. 引入跨域注解

import java.util.List;

@RestController
@RequestMapping("/api/java")
@CrossOrigin(origins = "*") // 2. 📢 核心：加上这行注解，允许前端 5173 端口跨域访问
public class JavaController {

    private final JavaService javaService;

    public JavaController(JavaService javaService) {
        this.javaService = javaService;
    }

    @GetMapping("/list")
    public ApiResponse<List<JavaInfo>> list() {
        List<JavaInfo> list = javaService.getJavaList();
        return ApiResponse.success(list);
    }

    /**
     * 新增：完美契合你原生 fail(msg) 方法的自动检测接口
     */
    @PostMapping("/detect")
    public ApiResponse<List<JavaInfo>> detect() {
        List<JavaInfo> list = javaService.getJavaList();

        if (list != null && !list.isEmpty()) {
            // 扫描到有 Java 环境，返回 code = 0
            return ApiResponse.success(list);
        } else {
            // 完美调用你的原生 fail 方法！返回 code = 500 和错误提示
            return ApiResponse.fail("未在系统中检测到任何有效的 Java 环境");
        }
    }
}