package com.xzy.springbootdisrutordemo.config.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

public class DisruptorBean {
    private Disruptor<MessageEvent> disruptor;
    private RingBuffer<MessageEvent> ringBuffer;

    public void init(){
        System.out.println("============初始化============");
        //ProducerType.MULTI 支持多事件发布
        disruptor = new Disruptor<MessageEvent>(new EventFactory<MessageEvent>() {
            @Override
            public MessageEvent newInstance() {
                return new MessageEvent();
            }
        }, 1024 * 2 , Executors.newFixedThreadPool(4) , ProducerType.MULTI ,new YieldingWaitStrategy());
        ringBuffer = disruptor.getRingBuffer();
        //定义异常处理
        disruptor.handleExceptionsWith(new ExceptionHandler<MessageEvent>() {
            @Override
            public void handleEventException(Throwable throwable, long l, MessageEvent messageEvent) {
                //TODO 待处理
            }

            @Override
            public void handleOnStartException(Throwable throwable) {
                //TODO 待处理
            }

            @Override
            public void handleOnShutdownException(Throwable throwable) {
                //TODO 待处理
            }
        });



        System.out.println("============初始化结束============");
    }

    public void destroy(){
        System.out.println("============销毁============");
    }

    public Disruptor<MessageEvent> getDisruptor() {
        return disruptor;
    }

    public void setDisruptor(Disruptor<MessageEvent> disruptor) {
        this.disruptor = disruptor;
    }

    public RingBuffer<MessageEvent> getRingBuffer() {
        return ringBuffer;
    }

    public void setRingBuffer(RingBuffer<MessageEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
}
