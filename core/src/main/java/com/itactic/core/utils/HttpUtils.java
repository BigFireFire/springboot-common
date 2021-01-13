package com.itactic.core.utils;

import com.itactic.core.constants.HttpMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author 1Zx.
 * @date 2021/1/13 10:51
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final Integer READ_TIME_OUT = 5000;
    private static final Integer CONN_TIME_OUT = 5000;
    private static final String SSLPrefix = "https";

    private HttpUtils() {
    }

    public static String get (String uri) {
        return get(uri, null, null);
    }

    public static String post (String uri) {
        return post(uri, null, null);
    }

    public static String put (String uri) {
        return put(uri, null, null);
    }

    public static String delete (String uri) {
        return delete(uri, null, null);
    }

    public static void download (String uri, File outFile) {
        doRequest(uri, HttpMethod.GET, null, null, null, null, outFile);
    }

    public static String get (String uri, Map<String, String> header, Object body) {
        return doRequest(uri, HttpMethod.GET, header, body, null, null);
    }

    public static String post (String uri, Map<String, String> header, Object body) {
        return doRequest(uri, HttpMethod.POST, header, body,null, null);
    }

    public static String put (String uri, Map<String, String> header, Object body) {
        return doRequest(uri, HttpMethod.PUT, header, body, null, null);
    }

    public static String delete (String uri, Map<String, String> header, Object body) {
        return doRequest(uri, HttpMethod.DELETE, header, body, null, null);
    }

    /**
     * 通用请求
     * @param uri
     * @see HttpMethod
     * @param httpMethod 请求方法
     * @param header
     * @param body
     * @param connTimeOut
     * @param readTimeOut
     * @return
     */
    public static String doRequest(String uri, HttpMethod httpMethod, Map<String, String> header, Object body, Integer connTimeOut, Integer readTimeOut) {
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        if (null == httpMethod) {
            logger.error("--------Http请求错误，无请求方法--------");
            return null;
        }
        if (null == connTimeOut) {
            connTimeOut = HttpUtils.CONN_TIME_OUT;
        }
        if (null == readTimeOut) {
            readTimeOut = HttpUtils.READ_TIME_OUT;
        }
        try {
            URL url = new URL(uri);
            if (SSLPrefix.equals(url.getProtocol())) {
                trustAllHttpsCertificates();
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(httpMethod.name());
            conn.setConnectTimeout(connTimeOut);
            conn.setReadTimeout(readTimeOut);
            conn.setDoInput(true);
            if (null != header && header.size() > 0) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (null != body) {
                conn.setDoOutput(true);
                os = conn.getOutputStream();
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                is = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String tmp;
                while (null != (tmp = br.readLine())) {
                    sb.append(tmp);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            logger.error("--------Http请求发生错误：链接：【{}】，请求方法：【{}】，请求头：【{}】，请求体：【{}】，错误信息：【{}】--------", uri, httpMethod, header, body, e.getMessage());
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
            if (null != conn) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * 通用下载请求
     * @param uri
     * @see HttpMethod
     * @param httpMethod 请求方法
     * @param header
     * @param body
     * @param connTimeOut
     * @param readTimeOut
     * @param outFile 输出文件
     */
    public static void doRequest (String uri, HttpMethod httpMethod, Map<String, String> header, Object body, Integer connTimeOut, Integer readTimeOut, File outFile) {
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        OutputStream fileOS = null;
        if (null == httpMethod) {
            logger.error("--------Http请求错误，无请求方法--------");
            return ;
        }
        if (null == connTimeOut) {
            connTimeOut = HttpUtils.CONN_TIME_OUT;
        }
        if (null == readTimeOut) {
            readTimeOut = HttpUtils.READ_TIME_OUT;
        }
        if (null == outFile) {
            logger.error("--------Http下载错误：输出文件不能为空--------");
            return;
        }
        try {
            URL url = new URL(uri);
            if (SSLPrefix.equals(url.getProtocol())) {
                trustAllHttpsCertificates();
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(httpMethod.name());
            conn.setConnectTimeout(connTimeOut);
            conn.setReadTimeout(readTimeOut);
            conn.setDoInput(true);
            if (null != header && header.size() > 0) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (null != body) {
                conn.setDoOutput(true);
                os = conn.getOutputStream();
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                is = conn.getInputStream();
                fileOS = new FileOutputStream(outFile);
                byte[] bs = new byte[1024];
                int len;
                while (-1 != (len = is.read(bs))) {
                    fileOS.write(bs, 0, len);
                }
            }
        } catch (Exception e) {
            logger.error("--------Http请求发生错误：链接：【{}】，请求方法：【{}】，请求头：【{}】，请求体：【{}】，输出文件目录：【{}】，错误信息：【{}】--------", uri, httpMethod, header, body, outFile.getPath(), e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(fileOS);
            IOUtils.closeQuietly(is);
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    private static void trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustManagers = new TrustManager[1];
        trustManagers[0] = new TrustAllManager();
        SSLContext sslContext = SSLContext.getInstance("TLSv1.1");
        sslContext.init(null, trustManagers, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    private static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public static void main(String[] args) {

    }
}
