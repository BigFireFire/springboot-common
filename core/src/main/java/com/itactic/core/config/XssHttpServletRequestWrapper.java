package com.itactic.core.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getHeader(String name) {
		if (name.endsWith("WithHtml")) {
			return super.getHeader(name);
		}
		String value = super.getHeader(name);
		if (value != null) {
			return HtmlUtils.htmlEscape(value);
		}
		return null;
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (name.endsWith("WithHtml")) {
			return value;
		}
		if (isJSONValid(value)) {
			return value;
		}
		return HtmlUtils.htmlEscape(value);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (name.endsWith("WithHtml")) {
			return values;
		}
		if (values != null) {
			int length = values.length;
			String[] escapseValues = new String[length];
			for (int i = 0; i < length; i++) {
				if (isJSONValid(values[i])) {
					escapseValues[i] = values[i];
				} else {
					escapseValues[i] = HtmlUtils.htmlEscape(values[i]);
				}
			}
			return escapseValues;
		}
		return super.getParameterValues(name);
	}

	public final static boolean isJSONValid(String value) {
		try {
			JSONObject.parseObject(value);
		} catch (Exception ex) {
			try {
				JSONObject.parseArray(value);
			} catch (Exception ex1) {
				return false;
			}
		}
		return true;
	}
}
