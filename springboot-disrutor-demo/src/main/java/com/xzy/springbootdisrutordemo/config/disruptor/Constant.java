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

    public  String bootstrapServers;

    public  String keyDeserializer;

    public  String valueDeserializer;

    public  String groupId;

    public  String autoCommit;

    public  String maxPollRecords;

    public  String beatPartitionNumber;

    public String beatlogSwith;

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

    @Value("${beatlog.kafka.partitionNumber}")
    public void setBeatPartitionNumber(String beatPartitionNumber) {
        this.beatPartitionNumber = beatPartitionNumber;
    }

    public String getBeatlogSwith() {
        return beatlogSwith;
    }

    @Value("${beatlog.kafka.switch}")
    public void setBeatlogSwith(String beatlogSwith) {
        this.beatlogSwith = beatlogSwith;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getAutoCommit() {
        return autoCommit;
    }

    public String getMaxPollRecords() {
        return maxPollRecords;
    }

    public String getBeatPartitionNumber() {
        return beatPartitionNumber;
    }
}
