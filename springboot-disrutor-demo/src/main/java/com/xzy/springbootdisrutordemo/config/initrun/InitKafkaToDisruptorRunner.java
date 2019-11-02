package com.xzy.springbootdisrutordemo.config.initrun;

import com.xzy.springbootdisrutordemo.config.disruptor.Constant;
import com.xzy.springbootdisrutordemo.config.disruptor.DisruptorBean;
import com.xzy.springbootdisrutordemo.config.disruptor.MessageEventToDisruptorThread;
import com.xzy.springbootdisrutordemo.util.SpringContextUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * @author xuzhiyong
 * @createDate 2019-11-02-10:00
 * 启动springboot后 启动kafka消费进程
 */
@Component
public class InitKafkaToDisruptorRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        DisruptorBean disruptorBean = SpringContextUtil.getBean(DisruptorBean.class);
        //启动消费线程，从kafka上获取数据
        for(int i = 0 ; i < 4 ; i++){
            Constant.publishMessageExec.submit(new MessageEventToDisruptorThread(disruptorBean.getRingBuffer()));
        }
    }
}
