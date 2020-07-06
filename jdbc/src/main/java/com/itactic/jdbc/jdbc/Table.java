package com.itactic.jdbc.jdbc;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String value() default "";
	String pks() default "";
	int tableSort() default 0;
	String tableForeignKey() default "";
}
