package com.xzy.springbootdisrutordemo.config.initrun;

import com.xzy.springbootdisrutordemo.config.disruptor.Constant;
import com.xzy.springbootdisrutordemo.config.disruptor.DisruptorBean;
import com.xzy.springbootdisrutordemo.config.disruptor.MessageEventToDisruptorThread;
import com.xzy.springbootdisrutordemo.util.KafkaUtil;
import com.xzy.springbootdisrutordemo.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author xuzhiyong
 * @createDate 2019-11-02-10:00
 * 启动springboot后 启动kafka消费进程
 */
@Component
public class InitKafkaToDisruptorRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        DisruptorBean disruptorBean = SpringContextUtil.getBean(DisruptorBean.class);
        Constant constant = SpringContextUtil.getBean(Constant.class);
        //启动消费线程，从kafka上获取数据
        //一般比较常用得方法是一个线程处理一个主题的分区 ，所以这里需要获取对应的主题的分区数然后确定线程大小
        //但这种方式无法应对新增分区的情况，如果有新分区需要做其他处理
        List<PartitionInfo> partitionInfoList = KafkaUtil.getTopicMetadata("httplog");
        if (partitionInfoList != null && StringUtils.equals("ON", constant.beatlogSwith)) {
            for (int i = 0; i < partitionInfoList.size(); i++) {
                Constant.publishMessageExec.submit(new MessageEventToDisruptorThread(disruptorBean.getRingBuffer(), i));
            }
        }

    }
}
