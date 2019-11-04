package com.xzy.springbootdisrutordemo.util;

import com.xzy.springbootdisrutordemo.config.disruptor.Constant;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Properties;

public class KafkaUtil {
    /***
     * 获取主题分区信息
     * @param topic
     * @return
     */
    public static List<PartitionInfo> getTopicMetadata( String topic) {
        Constant constant = SpringContextUtil.getBean(Constant.class);
        Properties props = new Properties();
        props.put("bootstrap.servers", constant.bootstrapServers);
        props.put("key.deserializer", constant.keyDeserializer);
        props.put("value.deserializer", constant.valueDeserializer);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        consumer.close();
        return partitionInfos;
    }
}
