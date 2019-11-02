package com.xzy.springbootdisrutordemo.config;

import com.xzy.springbootdisrutordemo.config.disruptor.DisruptorBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * 通用配置
 */
@Configuration
public class CommonConfiguration {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    public DisruptorBean getDisruptorBean(){
        return new DisruptorBean();
    }

}
