package com.itactic.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author 楼中煊
 * @date 2019年10月11日 下午4:08:42
 */
@Component
public final class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> className) {
        return context.getBean(className);
    }

    public static <T> T getBean (String beanName, Class<T> cls) {
        return context.getBean(beanName, cls);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

}
