package com.xzy.springbootcommondemo.init;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/***
 * 当ApplicationContext被初始化或刷新时，会触发ContextRefreshedEvent事件
 * 一般用来启动时初始化一些东西
 */
@Component
public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("====spring启动====");
    }
}
