package com.itactic.core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * 线程内共享HttpServletRequest和HttpServletResponse
 */
public class WebContext {
	private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
	private static final ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();

	public static void setRequest(HttpServletRequest req) {
		request.set(req);
	}

	public static final HttpServletRequest getRequest() {
		return request.get();
	}

	public static void removeRequest() {
		request.remove();
	}

	public static void setResponse(HttpServletResponse rep) {
		response.set(rep);
	}

	public static final HttpServletResponse getResponse() {
		return response.get();
	}

	public static void removeResponse() {
		response.remove();
	}

	public static final HttpSession getSession() {
		return request.get().getSession();
	}

	public static String getVal(String key) {
		return getRequest().getParameter(key);
	}

	public static boolean isAjax(HttpServletRequest request) {
		return (request.getHeader("accept") != null && request.getHeader("accept").indexOf("application/json") > -1)
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1);
	}
}
