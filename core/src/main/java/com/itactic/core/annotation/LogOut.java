package com.itactic.core.annotation;


import java.lang.annotation.*;

/**
 * @author 1Zx.
 * @date 2020/4/13 14:38
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOut {

    public LogLevel logLevel() default LogLevel.INFO;

    public boolean ignore() default false;
}
