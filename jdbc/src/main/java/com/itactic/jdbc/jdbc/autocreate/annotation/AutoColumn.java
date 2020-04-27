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
    String fieldName();

    /** 字段类型 */
    String filedType() default "undefine";

    /** 字段属性长度 */
    int fieldLength() default 255;

    boolean fieldNullAble() default true;

    /** 注释 */
    String fieldComment() default "";

}
