package com.liangzhicheng.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONUtil {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    public static String toJSONString(Object obj) {
        try {
            return DEFAULT_OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[JSON序列化] 序列化 JSON 失败", e);
            return "";
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        if (ToolUtil.isBlank(json)) {
            return null;
        }
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("[JSON序列化] 反序列化 JSON 失败", e);
            return null;
        }
    }

}
