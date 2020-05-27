package com.itactic.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetJsonUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(GetJsonUtil.class);
	
	public static String getJSONData(HttpServletRequest request){
		InputStream is = null;
		BufferedReader reader = null;
		StringBuilder data = new StringBuilder();
		String line = null;
		try {
			is = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			while ((line = reader.readLine()) != null) {
				data.append(line);
			}
		} catch (IOException e) {
			LOG.error("json解析失败：【{}】", e.getMessage());
		} finally {
			try {
				if(null != is){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return data.toString();
	}
}
