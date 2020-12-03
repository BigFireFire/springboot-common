package com.itactic.core.config;

import com.itactic.core.exception.BootCustomException;
import com.itactic.core.exception.ImportException;
import com.itactic.core.model.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionConfig {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionConfig.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public AjaxResult<String> handleException(HttpServletRequest request, Exception e) {
        logger.error("【系统异常】：请求路径：【{}】，错误信息：【{}】",request.getRequestURI(), e.getMessage());
        if (e instanceof BootCustomException || e instanceof ImportException) {
            return AjaxResult.error(e.getMessage());
        } else if (e instanceof NoHandlerFoundException) {
            return AjaxResult.error("接口不存在");
        } else if (e instanceof MissingServletRequestParameterException) {
            return AjaxResult.error(String.format("缺少【%s】参数，参数类型：【%s】",((MissingServletRequestParameterException) e).getParameterName(), ((MissingServletRequestParameterException) e).getParameterType() ));
        } else if ( e instanceof HttpMessageNotReadableException) {
            return AjaxResult.error("请求体不合法");
        } else if ( e instanceof MethodArgumentTypeMismatchException) {
            return AjaxResult.error("参数类型不合法");
        } else {
            return AjaxResult.error("系统错误");
        }
    }
}