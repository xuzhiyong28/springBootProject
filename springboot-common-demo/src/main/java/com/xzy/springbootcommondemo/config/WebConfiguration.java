package com.xzy.springbootcommondemo.config;

import com.xzy.springbootcommondemo.interceptor.MyInterceptor;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport{

    @Autowired
    private MyInterceptor myInterceptor;


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/");
        super.addInterceptors(registry);
    }

    /***
     * 配置静态资源,避免静态资源请求被拦截
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        super.addResourceHandlers(registry);
    }

    @Bean
    public RemoteIpFilter remoteIpFilter(){
        return new RemoteIpFilter();
    }

    /***
     * 设置过滤器1
     * @return
     */
    @Bean
    public FilterRegistrationBean  filterRegistration(){
        FilterRegistrationBean  registration = new FilterRegistrationBean();
        registration.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                System.out.println("this is MyFilter,url :"+request.getRequestURI());
                filterChain.doFilter(servletRequest, servletResponse);
            }
        });
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("MyFilter");
        registration.setOrder(1);
        return registration;
    }

    /***
     * 过滤器2
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistration2(){
        FilterRegistrationBean  registration = new FilterRegistrationBean();
        registration.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                System.out.println("this is MyFilter2,url :"+request.getRequestURI());
                filterChain.doFilter(servletRequest, servletResponse);
            }
        });
        registration.addInitParameter("paramName", "paramValue2");
        registration.setName("MyFilter2");
        registration.setOrder(2);
        return registration;
    }
}
