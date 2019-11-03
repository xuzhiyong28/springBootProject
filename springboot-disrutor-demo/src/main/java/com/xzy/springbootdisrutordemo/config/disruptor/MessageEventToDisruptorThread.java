package com.xzy.springbootdisrutordemo.config.disruptor;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.xzy.springbootdisrutordemo.util.SpringContextUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;

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
    private int partitionIndex; //分区的编号
    private static final String TOPIC;
    private static KafkaConsumer<String, String> kafkaConsumer;

    static {
        TOPIC = "beatlog";
    }

    public MessageEventToDisruptorThread(RingBuffer<MessageEvent> ringBuffer , int partitionIndex){
        Constant constant = SpringContextUtil.getBean(Constant.class);
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, constant.bootstrapServers); //kafka集群地址
        properties.put("group.id", constant.groupId + System.currentTimeMillis());
        properties.put("enable.auto.commit", constant.autoCommit); //显示偏移量自动提交
        properties.put("key.deserializer", constant.keyDeserializer);
        properties.put("value.deserializer", constant.valueDeserializer);
        properties.put("max.poll.records", constant.maxPollRecords); //每次拉取的最大条数
        properties.put("auto.offset.reset", "earliest");
        this.kafkaConsumer = new KafkaConsumer<>(properties);
        this.ringBuffer = ringBuffer;
        this.partitionIndex = partitionIndex;
    }



    @Override
    public void run() {
        //kafkaConsumer.subscribe(Arrays.asList(TOPIC));
        kafkaConsumer.assign(Lists.newArrayList(new TopicPartition(TOPIC , partitionIndex)));
        try{
            while (true){
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    //System.out.printf(" ThreadId = %s,partition = %s,offset = %d, key = %s, timestamp = %d , value = %s%n",
                    //        Thread.currentThread().getId(), record.partition(), record.offset(), record.key(), record.timestamp(), record.value());
                    EventTranslatorOneArg<MessageEvent, ConsumerRecord<String, String>> translator = new MessageEventTranslator();
                    //进入队列处理
                    ringBuffer.publishEvent(translator,record);
                }
                TimeUnit.MICROSECONDS.sleep(500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
