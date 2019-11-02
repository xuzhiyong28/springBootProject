package com.xzy.springbootdisrutordemo.config.disruptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xuzhiyong
 * @createDate 2019-11-02-10:07
 */
@Component
public class Constant {
    public static final ExecutorService publishMessageExec = Executors.newFixedThreadPool(4);

    public static String bootstrapServers;

    public static String keyDeserializer;

    public static  String valueDeserializer;

    public static  String groupId;

    public static  String autoCommit;

    public static String maxPollRecords;

    @Value("${beatlog.kafka.bootstrap.servers}")
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Value("${beatlog.kafka.key.deserializer}")
    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    @Value("${beatlog.kafka.value.deserializer}")
    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    @Value("${beatlog.kafka.group.id}")
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Value("${beatlog.kafka.enable.auto.commit}")
    public void setAutoCommit(String autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Value("${beatlog.max.poll.records}")
    public void setMaxPollRecords(String maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }
}
