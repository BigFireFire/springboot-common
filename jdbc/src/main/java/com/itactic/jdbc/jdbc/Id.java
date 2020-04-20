package com.itactic.jdbc.jdbc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
	GenerationType type() default GenerationType.UUID;

	String sequence() default "";
	
	/**
	 * 时间戳类型，默认为yyyyMMdd
	 * @return
	 */
	DateFormatType dateFormat() default DateFormatType.yyyyMMdd;
	
	/**
	 * 数据库类型，默认为ORACLE
	 * @return
	 */
	DBType dbType() default DBType.ORACLE;

	enum GenerationType {
		UUID, 
		SEQUENCE,
		/**时间戳加SEQUENCE类型*/
		TIMEANDSEQUENCE
	}
	
	/**
	 * 时间格式类型
	 */
	enum DateFormatType {
		yyyy,
		yyyyMM,
		yyyyMMdd,
		yyyyMMddHH,
		yyyyMMddHHmm,
		yyyyMMddHHmmss;
		String getDateFormatType(DateFormatType dateFormatType){
			if(dateFormatType == yyyy){
				return "yyyy";
			}else if(dateFormatType == yyyyMM){
				return "yyyyMM";
			}else if(dateFormatType == yyyyMMdd){
				return "yyyyMMdd";
			}else if(dateFormatType == yyyyMMddHH){
				return "yyyyMMddHH";
			}else if(dateFormatType == yyyyMMddHHmm){
				return "yyyyMMddHHmm";
			}else if(dateFormatType == yyyyMMddHHmmss){
				return "yyyyMMddHHmmss";
			}
			return "yyyyMMdd";
		}
	}
	
	/**
	 * 数据库类型
	 */
	enum DBType {
		ORACLE,
		MYSQL
	}
}
