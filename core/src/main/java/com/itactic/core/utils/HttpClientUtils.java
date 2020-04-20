/**
 * 
 */
package com.itactic.core.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientUtils {

	private static Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

	private static int HTTP_DEFAULT_TIMEOUT = 3000;

	public static String post(String postURL, Map<String, String> paramMap) {
		return post(postURL, paramMap, HTTP_DEFAULT_TIMEOUT);
	}

	/**
	 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	 * 
	 * @param postURL
	 * @param paramMap
	 */
	public static String post(String postURL, Map<String, String> paramMap, int timeout) {
		String result = null;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(postURL);
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (null != paramMap && !paramMap.isEmpty()) {
			for (Entry<String, String> param : paramMap.entrySet()) {
				formparams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
		}
		UrlEncodedFormEntity uefEntity = null;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>> executing request " + httppost.getURI());
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.build();
			httppost.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8");
					LOG.info("<<<<<<<<<<<<<<<<<<<<<<< Response content: " + result);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			LOG.error("<<<<<<<<<<<<<<<<<<<<<<< HttpClient post [{}] error: {}", httppost.getURI(), e.getMessage(), e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error("<<<<<<<<<<<<<<<<<<<<<<< HttpClient insert log error: {}", e);
			}
		}
		return result;
	}

	public static String get(String getURI) {
		return get(getURI, HTTP_DEFAULT_TIMEOUT);
	}

	/**
	 * 发送 get请求
	 */
	public static String get(String getURI, int timeout) {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(getURI);
			LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>> executing request " + httpget.getURI());
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.build();
			httpget.setConfig(requestConfig);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				// 打印响应状态
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8");
					// 打印响应内容
					LOG.info("<<<<<<<<<<<<<<<<<<<<<<< Response content: " + result);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			LOG.error("<<<<<<<<<<<<<<<<<<<<<<< HttpClient get [{}] error: {}", getURI, e.getMessage(), e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error("<<<<<<<<<<<<<<<<<<<<<<< HttpClient insert log error: {}", e);
			}
		}
		return result;
	}

	public static String httpGet(String url) {
		// get请求返回结果
		String result = null;
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			} else {
				LOG.error("get请求提交失败:" + url);
			}
		} catch (IOException e) {
			LOG.error("get请求提交失败:" + url, e);
		}
		return result;
	}

	public static void main(String[] args) {

	}
}
