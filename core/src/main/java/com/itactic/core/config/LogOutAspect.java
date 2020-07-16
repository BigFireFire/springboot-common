package com.itactic.core.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itactic.core.annotation.LogLevel;
import com.itactic.core.annotation.LogOut;
import com.itactic.core.model.AjaxResult;
import com.itactic.core.model.AjaxResultV2;
import com.itactic.core.utils.WebContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

/**
 * @author 1Zx.
 * @date 2020/4/13 17:38
 */
@Aspect
@Component
public final class LogOutAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** 正常返回日志格式 */
    private final String resultLog = "<<<<Class: [{}], Api: [{}], Result: [{}], Time: [{}]<<<<";
    /** 返回类型不受支持日志格式 */
    private final String resultLogUnSupport = "<<<<[{}]类的[{}]接口返回类型不受支持,返回时间：[{}]<<<<";
    /** 请求参数日志格式 */
    private final String paramsLog = ">>>>Class: [{}], Method: [{}], Api: [{}], Params: [{}], Time: [{}]>>>>";


    @Pointcut(value = "within(com..*.controller..*)")
    public void LogOut(){

    }

    @Pointcut(value = "bean(*Controller)")
    public void LogOutCls(){

    }

    @Before("LogOutCls() &&  @within(logOut)")
    public void clsBefore(JoinPoint joinPoint, LogOut logOut) {
        beforeLog(joinPoint,logOut);
    }


    @Before("LogOut() && @annotation(logOut)")
    public void before(JoinPoint joinPoint, LogOut logOut){
        Class cls = joinPoint.getTarget().getClass();
        LogOut logOutCls = (LogOut) cls.getAnnotation(LogOut.class);
        if (null == logOutCls) {
            beforeLog(joinPoint, logOut);
        }

    }

    @AfterReturning( value = "LogOut() && @annotation(logOut)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, LogOut logOut, Object result) {
        Class cls = joinPoint.getTarget().getClass();
        LogOut logOutCls = (LogOut) cls.getAnnotation(LogOut.class);
        if (null == logOutCls) {
            afterLog(joinPoint, logOut, result);
        }
    }

    @AfterReturning(value = "LogOutCls() && @within(logOut)", returning = "result")
    public void clsAfterReturning(JoinPoint joinPoint, LogOut logOut, Object result) {
        afterLog(joinPoint, logOut, result);
    }


    private void afterLog(JoinPoint joinPoint, LogOut logOut, Object result) {
        try {
            Class cls = joinPoint.getTarget().getClass();
            Optional<Object> optional = Optional.ofNullable(result);
            if (optional.isPresent() && (result instanceof AjaxResult || "AjaxResult".equals(result.getClass().getSimpleName()) || result instanceof AjaxResultV2)) {
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("----日志输出异常：[{}]----",e.getMessage());
        }
    }

    private void beforeLog(JoinPoint joinPoint, LogOut logOut) {
        try {
            JSONObject paramsJO = new JSONObject();
            Class cls = joinPoint.getTarget().getClass();
            if (null != joinPoint.getArgs() && joinPoint.getArgs().length > 0) {
                Object[] values = joinPoint.getArgs();
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                if (null != methodSignature) {
                    String[] paramNames = methodSignature.getParameterNames();
                    if (null != values && null != paramNames) {
                        if (values.length == paramNames.length && values.length > 0) {
                            for (int i = 0; i < paramNames.length; i++) {
                                Object object = values[i];
                                if (!(object instanceof HttpServletRequest) && !(object instanceof HttpServletResponse) && !(object instanceof MultipartFile) && !(object instanceof MultipartFile[])) {
                                    paramsJO.put(paramNames[i], object);
                                }
                            }
                        }
                    }
                }

            }
            HttpServletRequest request = WebContextUtils.getRequest();

            if (LogLevel.INFO.name().equals(logOut.logLevel().name())) {
                logger.info(paramsLog, cls.getSimpleName(), null != request ? request.getMethod() : "Wrong"
                        , joinPoint.getSignature().getName()
                        , paramsJO.toString(),
                        sdf.format(Calendar.getInstance().getTime()));
            } else if (LogLevel.DEBUG.name().equals(logOut.logLevel().name())) {
                logger.debug(paramsLog, cls.getSimpleName()
                        , joinPoint.getSignature().getName()
                        , paramsJO.toString(),
                        sdf.format(Calendar.getInstance().getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("----日志输出异常：[{}]----",e.getMessage());
        }
    }
}
