package com.itactic.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itactic.core.constants.BootConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class WebContextUtils extends WebContext {

	@Value("${login.use:session}")
	private String useLoginType;

	private static final Logger logger = LoggerFactory.getLogger(WebContextUtils.class);

	/**
	 * 获取用户token
	 * 
	 * @return
	 */
	public String getTokenString() {
		String tokenHeader = getRequest().getHeader(JwtTokenUtil.TOKEN_HEADER);
		if (StringUtils.isEmpty(tokenHeader)) {
			return null;
		}
		if (!tokenHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
			return null;
		}
		return tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
	}

	/**
	 * 获取用户
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSessionUser(@SuppressWarnings("rawtypes") Class clazz) {
		try {
			if ("session".equals(useLoginType)) {
				return (T) getSession().getAttribute(BootConstants.SESSION_INFO);
			}
			if ("jwt".equals(useLoginType)) {
				String user = JwtTokenUtil.getObjectId(getTokenString());
				T obj = null;
				if (user != null) {
					try {
						obj = (T) JSONObject.parseObject(user, clazz);
					} catch (Exception e) {
						obj = (T) user;
						logger.error(e.getMessage(), e);
					}
					return obj;
				}
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	// 获取用户对象
	public Object getSessionUser() {
		try {
			if ("session".equals(useLoginType)) {
				return getSession().getAttribute(BootConstants.SESSION_INFO);
			}
			if ("jwt".equals(useLoginType)) {
				String user = JwtTokenUtil.getObjectId(getTokenString());
				Object obj = user;
				if (user != null) {
					try {
						obj = JSONObject.parseObject(user);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
					return obj;
				}
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public String setSessionUser(String userid, Object obj, boolean isRememberMe) {
		try {
			String token = getSession().getId();
			if ("session".equals(useLoginType)) {
				getSession().setAttribute(BootConstants.SESSION_INFO, obj);
			}
			if ("jwt".equals(useLoginType)) {
				String objid = "";
				if (obj instanceof String) {
					objid = obj.toString();
				} else {
					objid = JSONObject.toJSONString(obj);
				}
				token = JwtTokenUtil.createToken(objid, userid, isRememberMe);
			}
			return token;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	// 重新拿最新的token
	public String refreshSessionUser(boolean isRememberMe) {
		try {
			String token = getSession().getId();
			if ("jwt".equals(useLoginType)) {
				String tokenHeader = getRequest().getHeader(JwtTokenUtil.TOKEN_HEADER);
				tokenHeader = getTokenString();
				String obj = JwtTokenUtil.getObjectId(tokenHeader);
				String username = JwtTokenUtil.getUsername(tokenHeader);
				token = JwtTokenUtil.createToken(obj, username, isRememberMe);
				JwtTokenUtil.setJwtExpiration(tokenHeader);
			}
			return token;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	// 获取token失效时间
	public Date getExpirationDate() {
		try {
			if ("jwt".equals(useLoginType)) {
				return JwtTokenUtil.getExpirationDate(getTokenString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	// 移除token，jwt无法移除
	public void removeSessionUser() {
		try {
			if ("session".equals(useLoginType)) {
				getSession().removeAttribute(BootConstants.SESSION_INFO);
			}
			if ("jwt".equals(useLoginType)) {
				JwtTokenUtil.setJwtExpiration(getTokenString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	// 判断token失效
	@SuppressWarnings("deprecation")
	public boolean isExpiration() {
		try {
			if ("session".equals(useLoginType)) {
				getSession().removeAttribute(BootConstants.SESSION_INFO);
			}
			if ("jwt".equals(useLoginType)) {
				return JwtTokenUtil.isExpiration(getTokenString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	// 移除token，jwt无法移除
	public void removeSessionUser(String tokenHeader) {
		try {
			if ("session".equals(useLoginType)) {
				getSession().removeAttribute(BootConstants.SESSION_INFO);
			}
			if ("jwt".equals(useLoginType)) {
				JwtTokenUtil.setJwtExpiration(getTokenString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	public Boolean isLogined() {
		Object user = getSessionUser();
		if (null == user) {
			return false;
		}
		return true;
	}

	public static void responseOutWithJson(HttpServletResponse response, Object object) {
		OutputStream out = null;
		try {
			String responseJSONObject = JSON.toJSONString(object);
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-type", "application/json;charset=UTF-8");
			out = response.getOutputStream();
			out.write(responseJSONObject.getBytes("utf-8"));
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
