package com.itactic.core.config;

import com.itactic.core.exception.BootCustomException;
import com.itactic.core.model.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ExceptionConfig {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionConfig.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public AjaxResult<String> handleException(Exception e) {
        if (e instanceof BootCustomException) {
            BootCustomException bootCustomException = (BootCustomException) e;
            logger.error("【BootCustomException】:{}", bootCustomException.getMessage());
            return AjaxResult.error("系统错误");
        } else if (e instanceof NoHandlerFoundException) {
            logger.error("【系统异常】:{}", e.getMessage());
            return AjaxResult.error("接口不存在");
        } else if (e instanceof MissingServletRequestParameterException) {
            logger.error("【系统异常】:{}", e.getMessage());
            return AjaxResult.error("缺少必填参数");
        } else if( e instanceof HttpMessageNotReadableException){
            logger.error("【系统异常】:{}", e.getMessage());
            return AjaxResult.error("请求参数不合法");
        }else  {
            logger.error("【系统异常】:{}", e.getMessage());
            return AjaxResult.error("系统错误");
        }
    }
}