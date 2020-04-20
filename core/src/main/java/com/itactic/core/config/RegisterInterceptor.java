package com.itactic.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class RegisterInterceptor implements WebMvcConfigurer {

	@Value("${system.swagger.enable:'false'}")
	private String swagger;
	@Value("${login.uri:/login}")
	private String loginUri;
	@Value("${login.static.paths:''}")
	private String excludePathPatterns;

	// 定义时间格式转换器
	@Bean
	public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		converter.setObjectMapper(mapper);
		return converter;
	}

	// 添加转换器
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(jackson2HttpMessageConverter());
	}

	@Bean
	public LoginInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration addInterceptor = registry.addInterceptor(loginInterceptor());
		// 排除静态资源
		if ("true".equals(swagger)) {
			addInterceptor.excludePathPatterns("/swagger-resources/**");
			addInterceptor.excludePathPatterns("/webjars/**");
			addInterceptor.excludePathPatterns("/v2/**");
			addInterceptor.excludePathPatterns("/swagger-ui.html/**");
			addInterceptor.excludePathPatterns("/doc.html/**");
		}
		if (StringUtils.isNotEmpty(excludePathPatterns)) {
			for (String path : excludePathPatterns.split(",")) {
				addInterceptor.excludePathPatterns("/" + path + "/**");
			}
		}
		addInterceptor.excludePathPatterns(loginUri);
		// 拦截配置
		addInterceptor.addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		if ("true".equals(swagger)) {
			registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
			registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
		}
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
