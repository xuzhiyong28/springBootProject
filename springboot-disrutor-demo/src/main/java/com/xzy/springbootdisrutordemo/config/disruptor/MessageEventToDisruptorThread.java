package com.xzy.springbootdisrutordemo.config.disruptor;

import com.lmax.disruptor.RingBuffer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiyong
 * @createDate 2019-11-02-9:33
 * 用来将消息发送到disruptorBean
 */
public class MessageEventToDisruptorThread implements Runnable{


    private RingBuffer<MessageEvent> ringBuffer;
    private static final String TOPIC;
    private static KafkaConsumer<String, String> kafkaConsumer;

    static {
        TOPIC = "beatlog";
    }

    public MessageEventToDisruptorThread(RingBuffer<MessageEvent> ringBuffer){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constant.bootstrapServers); //kafka集群地址
        properties.put("group.id", Constant.groupId);
        properties.put("enable.auto.commit", Constant.autoCommit); //显示偏移量自动提交
        properties.put("key.deserializer", Constant.keyDeserializer);
        properties.put("value.deserializer", Constant.valueDeserializer);
        properties.put("max.poll.records", Constant.maxPollRecords); //每次拉取的最大条数
        properties.put("auto.offset.reset", "earliest");
        this.kafkaConsumer = new KafkaConsumer<>(properties);
        this.ringBuffer = ringBuffer;
    }



    @Override
    public void run() {
        kafkaConsumer.subscribe(Arrays.asList(TOPIC));
        try{
            while (true){
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf(" ThreadId = %s ,offset = %d, key = %s, timestamp = %d , value = %s%n", Thread.currentThread().getId(), record.offset(), record.key(), record.timestamp(), record.value());
                }
                TimeUnit.MICROSECONDS.sleep(500);
            }
        }catch (Exception e){

        }
    }

}
