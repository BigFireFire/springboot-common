package com.itactic.core.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itactic.core.annotation.LogLevel;
import com.itactic.core.annotation.LogOut;
import com.itactic.core.model.AjaxResult;
import com.itactic.core.vo.CustomRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Optional;

/**
 * @author 1Zx.
 * @date 2020/4/13 17:38
 */
@Aspect
@Component
public final class LogOutAspect {

    private Logger logger;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** 正常返回日志格式 */
    private final String resultLog = ">>>>【{}】类的【{}】接口返回参数：【{}】,返回时间：【{}】<<<<";
    /** 返回类型不受支持日志格式 */
    private final String resultLogUnSupport = ">>>>【{}】类的【{}】接口返回类型不受支持,返回时间：【{}】<<<<";
    /** 请求参数日志格式 */
    private final String paramsLog = ">>>>【{}】类的【{}】接口调用参数：【{}】,调用时间：【{}】<<<<";


    @Pointcut(value = "bean(*Controller)")
    public void LogOut(){

    }

    @Pointcut(value = "within(com..*.controller.*)")
    public void LogOutCls(){

    }

    @Before("LogOutCls() && @within(logOut)")
    public void clsBefore(JoinPoint joinPoint, LogOut logOut) {
        beforeAspect(joinPoint,logOut);
    }


    @Before("LogOut() && @annotation(logOut)")
    public void before(JoinPoint joinPoint, LogOut logOut){
        Class cls = joinPoint.getTarget().getClass();
        LogOut logOutCls = (LogOut) cls.getAnnotation(LogOut.class);
        if (null == logOutCls) {
            beforeAspect(joinPoint, logOut);
        }

    }

    @AfterReturning( value = "LogOut() && @annotation(logOut)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, LogOut logOut, Object result) {
        Class cls = joinPoint.getTarget().getClass();
        LogOut logOutCls = (LogOut) cls.getAnnotation(LogOut.class);
        if (null == logOutCls) {
            afterReturningAspect(joinPoint, logOut, result);
        }
    }

    @AfterReturning(value = "LogOutCls() && @within(logOut)", returning = "result")
    public void clsAfterReturning(JoinPoint joinPoint, LogOut logOut, Object result) {
        afterReturningAspect(joinPoint, logOut, result);
    }


    private void afterReturningAspect(JoinPoint joinPoint, LogOut logOut, Object result) {
        Class cls = joinPoint.getTarget().getClass();
        logger = LoggerFactory.getLogger(cls);
        Optional<Object> optional = Optional.ofNullable(result);
        if (optional.isPresent() && (result instanceof AjaxResult || "AjaxResult".equals(result.getClass().getSimpleName()))) {
            if (LogLevel.INFO.name().equals(logOut.logLevel().name())) {
                logger.info(resultLog,cls.getSimpleName()
                        ,joinPoint.getSignature().getName()
                        , JSON.toJSON(result),
                        sdf.format(Calendar.getInstance().getTime()));
            } else if (LogLevel.DEBUG.name().equals(logOut.logLevel().name())) {
                logger.debug(resultLog,cls.getSimpleName()
                        ,joinPoint.getSignature().getName()
                        , JSON.toJSON(result),
                        sdf.format(Calendar.getInstance().getTime()));
            }
        } else {
            logger.info(resultLogUnSupport,cls.getSimpleName()
                    ,joinPoint.getSignature().getName(),
                    sdf.format(Calendar.getInstance().getTime()));
        }
    }

    private void beforeAspect(JoinPoint joinPoint, LogOut logOut) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        JSONObject paramsJO = new JSONObject();
        Class cls = joinPoint.getTarget().getClass();
        logger = LoggerFactory.getLogger(cls);
        switch (request.getMethod()) {
            case "GET":
                for (Enumeration<String> enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
                    String key = enumeration.nextElement();
                    paramsJO.put(key, request.getParameter(key));
                }
            break;
            default:
                if ("application/json".equals(request.getContentType())) {
                    Object[] objects = joinPoint.getArgs();
                    for (Object object : objects) {
                        if (object instanceof CustomRequest) {
                            paramsJO = JSON.parseObject(JSON.toJSONString(object));
                        }
                    }
                }
                break;
        }
        if (LogLevel.INFO.name().equals(logOut.logLevel().name())) {
            logger.info(paramsLog,cls.getSimpleName()
                    ,joinPoint.getSignature().getName()
                    ,paramsJO.toString(),
                    sdf.format(Calendar.getInstance().getTime()));
        } else if (LogLevel.DEBUG.name().equals(logOut.logLevel().name())) {
            logger.debug(paramsLog,cls.getSimpleName()
                    ,joinPoint.getSignature().getName()
                    ,paramsJO.toString(),
                    sdf.format(Calendar.getInstance().getTime()));
        }
    }
}
