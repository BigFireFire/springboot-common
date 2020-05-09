package com.itactic.core.config;

import com.itactic.core.exception.BootCustomException;
import com.itactic.core.model.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionConfig {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionConfig.class);

    @ExceptionHandler(value = Exception.class)
    public AjaxResult<String> handleException(Exception e) {
        if (e instanceof BootCustomException) {
            BootCustomException bootCustomException = (BootCustomException) e;
            logger.error("【BootCustomException】：{}", bootCustomException.getMessage());
            return AjaxResult.error("系统错误");
        } else {
            logger.error("【系统异常】{}", e.getMessage());
            return AjaxResult.error("系统错误");
        }
    }
}