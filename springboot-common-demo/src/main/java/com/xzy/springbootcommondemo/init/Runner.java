package com.xzy.springbootcommondemo.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/***
 * 自定义springboot启动初始化
 */
@Component
@Order(1)
public class Runner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("===========启动项目初始化1==============");
    }
}
