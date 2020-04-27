package com.itactic.jdbc.jdbc.autocreate.annotation;

import java.lang.annotation.*;

/**
 * @author 1Zx.
 * @date 2020/4/24 22:12
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoColumn {

    /** 字段名称 */
    String fieldName() default "";

    /** 字段类型+长度 */
    String filedType();

    boolean fieldNullAble() default true;

    /** 注释 */
    String fieldComment() default "";

}
