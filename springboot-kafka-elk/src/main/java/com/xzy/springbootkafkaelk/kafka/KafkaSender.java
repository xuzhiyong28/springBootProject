package com.xzy.springbootkafkaelk.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author xuzhiyong
 * @createDate 2019-10-19-20:21
 */
public class KafkaSender {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendChannelMess(String topic,String message){
        kafkaTemplate.send(topic, message);
    }
}
