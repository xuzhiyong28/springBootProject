package com.xzy.springbootdisrutordemo.config.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/***
 * 消息转化类， 将kafka的消息转换成MessageEvent
*/
public class MessageEventTranslator implements EventTranslatorOneArg<MessageEvent, ConsumerRecord<String, String>> {

    @Override
    public void translateTo(MessageEvent messageEvent, long l, ConsumerRecord<String, String> record) {
        messageEvent.setMessage(record.value());
    }
}
