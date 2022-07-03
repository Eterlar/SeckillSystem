package com.eterlar.seckillsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author eterlar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /**
     * 时间间隔
     */
    int seconds();

    /**
     * 短时间最大访问次数
     */
    int maxTimes();

    /**
     * 是否要求登录
     */
    boolean needLogin() default true;
}
