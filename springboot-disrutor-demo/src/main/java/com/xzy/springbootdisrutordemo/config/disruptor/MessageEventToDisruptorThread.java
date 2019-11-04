package com.xzy.springbootdisrutordemo.config.disruptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.xzy.springbootdisrutordemo.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiyong
 * @createDate 2019-11-02-9:33
 * 用来将消息发送到disruptorBean
 */
public class MessageEventToDisruptorThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageEventToDisruptorThread.class);
    private RingBuffer<MessageEvent> ringBuffer;
    private int partitionIndex; //分区的编号
    private static final String TOPIC;
    private static KafkaConsumer<String, String> kafkaConsumer;
    //用于跟踪当前主题的偏移量
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets;

    static {
        TOPIC = "httplog";
    }

    public MessageEventToDisruptorThread(RingBuffer<MessageEvent> ringBuffer, int partitionIndex) {
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
        this.currentOffsets = Maps.newHashMap();
    }


    @Override
    public void run() {
        Constant constant = SpringContextUtil.getBean(Constant.class);
        //kafkaConsumer.subscribe(Arrays.asList(TOPIC));
        //对指定分区消费数据
        kafkaConsumer.assign(Lists.newArrayList(new TopicPartition(TOPIC, partitionIndex)));
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    /*logger.info(" ThreadId = %s,partition = %s,offset = %d, key = %s, timestamp = %d , value = %s%n",
                            Thread.currentThread().getId(), record.partition(), record.offset(), record.key(), record.timestamp(), record.value());*/
                    EventTranslatorOneArg<MessageEvent, ConsumerRecord<String, String>> translator = new MessageEventTranslator();
                    //进入队列处理
                    ringBuffer.publishEvent(translator, record);
                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1, "no metadata"));
                }

                if (!StringUtils.equals(constant.autoCommit, "true")) {
                    //如果不是自动提交，我们需要每次获取后做手动提交
                    kafkaConsumer.commitAsync();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!StringUtils.equals(constant.autoCommit, "true")) {
                kafkaConsumer.commitSync();
            }
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
        }
    }

}
