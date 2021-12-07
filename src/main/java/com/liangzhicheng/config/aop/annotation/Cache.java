package com.liangzhicheng.config.aop.annotation;

import com.liangzhicheng.common.constant.Constants;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /**
     * 缓存key
     * 若assign为true，存入redis的键为key的值
     * 若assign为false，存入redis的的键为service方法的 key_className_methodName
     */
    String key() default Constants.MALL_CACHE;

    /**
     * 是否使用指定的key，若为true，存入redis的键为key的值
     */
    boolean assign() default false;

}
