//package com.itactic.core.utils;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * @author 1Zx.
// * @date 2020/7/7 11:00
// */
//@Scope("singleton")
//@Component
//public class DateFormatUtils {
//
//    private static String pattern;
//
//    public static Date timeToDate (Long timeMillis) {
//        if (null == timeMillis) {
//            return null;
//        }
//        return new Date(timeMillis);
//    }
//
//    public static Date timeToDate (String timeMillis) {
//        if (StringUtils.isBlank(timeMillis) || !StringUtils.isNumeric(timeMillis)) {
//            return null;
//        }
//        return timeToDate(Long.valueOf(timeMillis));
//    }
//
//    public static String dateFormat (Date date) {
//        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
//        return localDateTime.format(DateTimeFormatter.ofPattern(DateFormatUtils.pattern, Locale.CHINA));
//    }
//
//    @Value("${date.format.pattern:'yyyy-MM-dd HH:mm:ss'}")
//    public void setPattern(String pattern) {
//        DateFormatUtils.pattern = pattern;
//    }
//
//}
