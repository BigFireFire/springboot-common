package com.itactic.jdbc.jdbc;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceType {
	String value() default "";
	DbType type() default DbType.MYSQL;
}
