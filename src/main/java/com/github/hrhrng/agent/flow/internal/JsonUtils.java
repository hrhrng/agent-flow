package com.github.hrhrng.agent.flow.internal;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author xiaoyang
 **/
public class JsonUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
