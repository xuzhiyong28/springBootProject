package com.xzy.springbootdisrutordemo;

import com.xzy.springbootdisrutordemo.config.disruptor.Constant;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.misc.Signal;
import sun.misc.SignalHandler;

@SpringBootApplication
public class SpringbootDisrutorDemoApplication {

	public static void main(String[] args) {

		Signal sg = new Signal("TERM");
		Signal.handle(sg, signal -> System.exit(0));
		// 注册关闭钩子
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 执行收尾工作
            for(KafkaConsumer kafkaConsumer : Constant.kafkaConsumerLists){
            	if(kafkaConsumer != null){
            		kafkaConsumer.wakeup();
            		kafkaConsumer.close();
				}
			}
        }));

		SpringApplication.run(SpringbootDisrutorDemoApplication.class, args);
	}

}
