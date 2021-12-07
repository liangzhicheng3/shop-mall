package com.liangzhicheng.config.mvc.filter.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermFilter {

    /**
     * sql中数据创建用户（传入create_user_id）的别名
     */
    String userAlias() default "";

    /**
     * sql中数据创建用户部门（传入create_user_dept_id）的别名
     */
    String deptAlias() default "";

}
