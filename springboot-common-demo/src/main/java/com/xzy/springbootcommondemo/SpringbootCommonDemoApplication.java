package com.xzy.springbootcommondemo;

import com.xzy.springbootcommondemo.constant.StudentConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({StudentConstant.class})
public class SpringbootCommonDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCommonDemoApplication.class, args);
    }

}
