package com.itactic.core.config;

import com.itactic.core.annotation.DuplicateSubmitToken;
import com.itactic.core.exception.BootCustomException;
import com.itactic.core.utils.WebContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @description 防止表单重复提交拦截器
 */
@Aspect
@Component
public class DuplicateSubmitAspect {

	private static final Logger logger = LoggerFactory.getLogger(DuplicateSubmitAspect.class);

	private static final String REPEAT = "com.itactic.core.repeat";

	@Pointcut("within(com..*.controller..*)")
	public void duplicateSubmit() {
	}

	@Before("duplicateSubmit() && @annotation(token)")
	public void before(final JoinPoint joinPoint, DuplicateSubmitToken token) {
		if (token != null) {
			HttpServletRequest request = WebContextUtils.getRequest();
			HttpSession session = request.getSession();
			boolean isSaveSession = token.save();
			if (isSaveSession) {
				String TOKEN_KEY = String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName());
				Object t = session.getAttribute(TOKEN_KEY);
				if (null == t) {
					String uuid = UUID.randomUUID().toString();
					session.setAttribute(TOKEN_KEY, uuid);
				} else {
					logger.error("方法[{}]收到重复的请求", TOKEN_KEY);
					session.setAttribute(REPEAT, true);
					throw new BootCustomException("请不要重复请求！");
				}
			}
		}
	}

	@AfterReturning("duplicateSubmit() && @annotation(token)")
	public void doAfterReturning(JoinPoint joinPoint, DuplicateSubmitToken token) {
		if (token != null) {
			HttpServletRequest request = WebContextUtils.getRequest();
			boolean isSaveSession = token.save();
			if (isSaveSession) {
				String TOKEN_KEY = String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName());
				HttpSession session = request.getSession();
				Object t = session.getAttribute(TOKEN_KEY);
				if (null != t) {// 方法执行完毕移除请求重复标记
					session.removeAttribute(TOKEN_KEY);
					session.removeAttribute(REPEAT);
				}
			}
		}
	}

	@AfterThrowing("duplicateSubmit() && @annotation(token)")
	public void ex(JoinPoint joinPoint, DuplicateSubmitToken token) {
		if (token != null) {
			HttpServletRequest request = WebContextUtils.getRequest();
			boolean isSaveSession = token.save();
			if (isSaveSession) {
				String TOKEN_KEY = String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName());
				HttpSession session = request.getSession();
				Object t = session.getAttribute(TOKEN_KEY);
				Boolean repeat = (Boolean) request.getSession().getAttribute(REPEAT);
				if (null != t && (null == repeat || !repeat)) {// 方法执行完毕移除请求重复标记
					request.getSession().removeAttribute(TOKEN_KEY);
				}
			}
		}
	}
}
