package com.liangzhicheng.config.aop.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /**
     * 操作记录值
     */
    String operate() default "操作日志";

}
