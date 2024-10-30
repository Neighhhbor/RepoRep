package com.hxxdemo.githubLogin.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Json工具类
 */
public final class JsonUtil {

    private static ObjectMapper objMapper = null;

    /**
     * 不可实例化
     */
    private JsonUtil() {
    }

    /**
     * 对象转json字符串
     *
     * @param value
     * @return
     */
    public static String toJson(Object value) {
        try {
            return getObjMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json串转对象
     *
     * @param json      json串
     * @param valueType 对象类型
     * @return
     */
    public static <T> T toObject(String json, Class<T> valueType) {
        try {
            return getObjMapper().readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json串转对象
     *
     * @param json          json串
     * @param typeReference 对象类型
     * @return
     */
    public static <T> T toObject(String json, TypeReference<?> typeReference) {
        try {
            return getObjMapper().readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json串转对象
     *
     * @param json     json串
     * @param javaType 对象类型
     * @return
     */
    public static <T> T toObject(String json, JavaType javaType) {
        try {
            return getObjMapper().readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json串转具有指定泛型的List对象
     *
     * @param json      json串
     * @param valueType 对象类型
     * @return
     */
    public static List toList(String json, Class<?> valueType) {
        JavaType javaType = getObjMapper().getTypeFactory().constructParametricType(
                ArrayList.class, valueType);
        try {
            return (List) getObjMapper().readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换为json流
     *
     * @param writer writer
     * @param value  对象
     */
    public static void writeValue(Writer writer, Object value) {
        try {
            getObjMapper().writeValue(writer, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper getObjMapper() {
        if (objMapper == null) {
            objMapper = new ObjectMapper();
            objMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            objMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            // 枚举类型返回ordinal，而不是名称
            objMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
            // 遇到未知属性不报错
            objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //空对象不要抛出异常
            objMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
            objMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

            objMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }

        return objMapper;
    }
}
