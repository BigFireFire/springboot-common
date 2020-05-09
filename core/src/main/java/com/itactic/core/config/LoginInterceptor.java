package com.itactic.core.config;

import com.itactic.core.annotation.NoLogin;
import com.itactic.core.constants.BootConstants;
import com.itactic.core.model.AjaxResult;
import com.itactic.core.utils.WebContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	@Value("${login.uri:/login}")
	private String loginUri;

	@Value("${login.need:true}")
	private Boolean needLogin;

	@Value("${login.use:session}")
	private String useLoginType;

	@Value("${http.token.key:token}")
	private String httpTokenKey;

	@Resource
	private WebContextUtils webContextUtils;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		WebContextUtils.setRequest(request);
		WebContextUtils.setResponse(response);
		String requestURI = request.getRequestURI().replace(request.getContextPath(), "");
		if (requestURI.equals(loginUri)) {
			return true;
		}
		if (!needLogin) {
			return true;
		}
		Boolean need = false;
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			NoLogin nologin = handlerMethod.getMethod().getDeclaringClass().getAnnotation(NoLogin.class);
			if (nologin != null) {
				need = nologin.isNeedLogin();
			}
			if (!need) {
				NoLogin login = handlerMethod.getMethod().getAnnotation(NoLogin.class);
				if (login != null) {
					need = login.isNeedLogin();
				}
			}
		}
		if (handler instanceof AbstractHandlerMapping) {
			logger.info("====>>>>AbstractHandlerMapping");
			need = true;
		}
		if (need) {
			return true;
		}

		if ("jwt".equals(useLoginType)) {
			String token = request.getHeader(httpTokenKey);
			if (StringUtils.isNotBlank(token)) {
				return true;
			}
		} else if ("session".equals(useLoginType)) {
			Object user = webContextUtils.getSessionUser();
			if (user != null) {
				return true;
			}
		}

		String requestType = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(requestType)) {
			WebContextUtils.responseOutWithJson(response, AjaxResult.noLogin());
			return false;
		} else {
			response.sendRedirect(request.getContextPath() + loginUri);
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		try {
			WebContextUtils.removeRequest();
			WebContextUtils.removeResponse();
			if (ex != null) {
				logger.info("LoginInterceptor throws Exceptions ", ex);
				if (WebContextUtils.isAjax(request)) {
					WebContextUtils.responseOutWithJson(response, AjaxResult.error(ex.getMessage()));
				} else {
					request.setAttribute("status", BootConstants.AJAX_STATUS.error);
					request.setAttribute("msg", ex.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
