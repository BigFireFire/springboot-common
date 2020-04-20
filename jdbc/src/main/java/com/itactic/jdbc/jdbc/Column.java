package com.itactic.jdbc.jdbc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.FIELD) 
public @interface Column {
	
	String value();
}
