package com.itactic.core.utils;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtTokenUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

	private static final String SECRET = "itacticjwt";
	private static final String ISS = "leopard";

	// 过期时间是3600秒，既是1个小时
	public static final long EXPIRATION = 3600L;

	// 选择了记住我之后的过期时间为7天
	public static final long EXPIRATION_REMEMBER = 604800L;

	/**
	 * 创建token 注：如果是根据可变的唯一值来生成，唯一值变化时，需重新生成token
	 * 
	 * @param username
	 * @param isRememberMe
	 * @return
	 */
	public static String createToken(String id, String username, boolean isRememberMe) {
		long expiration = isRememberMe ? EXPIRATION_REMEMBER : EXPIRATION;
		// 可以将基本不重要的对象信息放到claims中，此处信息不多,见简单直接放到配置内
		// HashMap<String,Object> claims = new HashMap<String,Object>();
		// claims.put("id", id);
		// claims.put("username",username);
		// id是重要信息，进行加密下
		return Jwts.builder().signWith(SignatureAlgorithm.HS512, SECRET)
				// 这里要早set一点，放到后面会覆盖别的字段
				// .setClaims(claims)
				.setIssuer(ISS).setId(id).setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000)).compact();
	}

	/**
	 * 从token中获取用户名
	 * 
	 * @param token
	 * @return
	 */
	public static String getUsername(String token) {
		Claims claims = getTokenBody(token);
		if (claims != null) {
			return claims.getSubject();
		} else {
			return null;
		}
	}

	/**
	 * 从token中获取ID，同时做解密处理
	 * 
	 * @param token
	 * @return
	 */
	public static String getObjectId(String token) {
		Claims claims = getTokenBody(token);
		if (claims != null) {
			return claims.getId();
		} else {
			return null;
		}
	}

	/**
	 * 是否已过期
	 * 
	 * @param token
	 * @throws 过期无法判断，只能通过捕获ExpiredJwtException异常
	 * @return
	 */
	@Deprecated
	public static boolean isExpiration(String token) {
		return getTokenBody(token).getExpiration().before(new Date());
	}

	/**
	 * 获取失效时间
	 * 
	 * @param token
	 * @throws 过期无法判断，只能通过捕获ExpiredJwtException异常
	 * @return
	 */
	public static Date getExpirationDate(String token) {
		return getTokenBody(token).getExpiration();
	}

	/**
	 * 设置失效
	 * 
	 * @param token
	 * @throws 自定义UserLoginException异常处理
	 * @return
	 */
	public static void setJwtExpiration(String token) {
		Claims claims = getTokenBody(token);
		if (claims != null) {
			claims.clear();
		}
	}

	/**
	 * 获取token信息，同时也做校验处理
	 * 
	 * @param token
	 * @throws 自定义UserLoginException异常处理
	 * @return
	 */
	public static Claims getTokenBody(String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				return null;
			}
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
			return claims;
		} catch (ExpiredJwtException expired) {
			logger.error("token[{}]已过期", token);
			return null;
		} catch (SignatureException e) {
			logger.error("token[{}]无效", token);
			return null;
		} catch (MalformedJwtException malformedJwt) {
			logger.error("token[{}]无效", token);
			return null;
		}
	}

}
