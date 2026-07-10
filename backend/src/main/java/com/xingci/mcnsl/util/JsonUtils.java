package com.xingci.mcnsl.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JSON 工具类
 */
public final class JsonUtils {

    /**
     * 全局 ObjectMapper
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {

        // 忽略未知字段
        MAPPER.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );

        // 不输出 null
        MAPPER.setSerializationInclusion(
                JsonInclude.Include.NON_NULL
        );

        // 美化输出
        MAPPER.enable(
                SerializationFeature.INDENT_OUTPUT
        );

    }

    private JsonUtils() {
    }

    /**
     * 获取全局 Mapper
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * JSON -> 对象
     */
    public static <T> T read(String json, Class<T> clazz)
            throws JsonProcessingException {

        return MAPPER.readValue(json, clazz);
    }

    /**
     * 文件 -> 对象
     */
    public static <T> T read(Path file, Class<T> clazz)
            throws IOException {

        return MAPPER.readValue(file.toFile(), clazz);
    }

    /**
     * InputStream -> 对象
     */
    public static <T> T read(
            java.io.InputStream input,
            Class<T> clazz
    ) throws IOException {

        return MAPPER.readValue(input, clazz);
    }

    /**
     * 对象 -> JSON
     */
    public static String toJson(Object object)
            throws JsonProcessingException {

        return MAPPER.writeValueAsString(object);
    }

    /**
     * 保存 JSON
     */
    public static void write(Path file, Object object)
            throws IOException {

        Files.createDirectories(file.getParent());

        MAPPER.writeValue(file.toFile(), object);
    }

}