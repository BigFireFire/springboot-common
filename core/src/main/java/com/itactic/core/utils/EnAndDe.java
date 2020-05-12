package com.itactic.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 加密解密工具类
 */
public class EnAndDe {
	private static Logger log = LoggerFactory.getLogger(EnAndDe.class);

	private static final String UTF = "UTF-8";
	
	/**
	 * base64解密
	 * @param data
	 * @return
	 */
	public static String baseDe(String data){
		try {
			return new String(Base64.decodeBase64(data.getBytes(UTF)), UTF);
		} catch (UnsupportedEncodingException e) {
			log.error("base64解密异常", e);
			return null;
		}
	}
	
	/**
	 * base64加密
	 * @param data
	 * @return
	 */
	public static String baseEn(String data){
		try {
			return new String(Base64.encodeBase64(data.getBytes(UTF)), UTF);
		} catch (UnsupportedEncodingException e) {
			log.error("base64加密异常", e);
			return null;
		}
	}
	/**
	 * 获得md5
	 * @param plainText
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getMd5(String plainText) throws UnsupportedEncodingException
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes("utf-8"));
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++)
            {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

	public static String RandomString(int length) {  
	    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
	    Random random = new Random();  
	    StringBuffer buf = new StringBuffer();  
	    for (int i = 0; i < length; i++) {  
	        int num = random.nextInt(str.length());
	        buf.append(str.charAt(num));  
	    }  
	    return buf.toString();  
	}

	public static String RandomNumString(int length) {
		String str = "0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(str.length());
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	public static void main(String[] args) throws Exception{

	}
}
