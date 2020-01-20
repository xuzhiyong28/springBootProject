package com.xzy.springbootdisrutordemo.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author xuzhiyong
 * @createDate 2019-11-02-10:02
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext; // Spring应用上下文环境

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<?> clz) throws BeansException {
        return (T) applicationContext.getBean(clz);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeansOfType(Class<T> clz){
        return applicationContext.getBeansOfType(clz);
    }

}
