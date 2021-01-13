package com.itactic.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 1Zx.
 * @date 2020/7/7 11:00
 */
@Scope("singleton")
@Component
public class DateFormatUtils {

    private static String pattern;

    private static DateTimeFormatter dateTimeFormatter;

    public static Date format (Long timeMillis) {
        if (null == timeMillis) {
            return null;
        }
        return new Date(timeMillis);
    }

    public static Date format (String timeMillis) {
        if (StringUtils.isBlank(timeMillis) || !StringUtils.isNumeric(timeMillis)) {
            return null;
        }
        return format(Long.valueOf(timeMillis));
    }

    public static String format (Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return dateTimeFormatter.format(localDateTime);
    }

    @Value("${date.format.pattern:'yyyy-MM-dd HH:mm:ss'}")
    public void setPattern(String pattern) {
        DateFormatUtils.pattern = pattern;
        dateTimeFormatter = DateTimeFormatter.ofPattern(DateFormatUtils.pattern);
    }

}
