package com.xzy.springbootdisrutordemo.config.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

public class DisruptorBean {
    private Disruptor<MessageEvent> disruptor;
    private RingBuffer<MessageEvent> ringBuffer;
    private static final int threadPoolSize = 4;
    public void init(){
        System.out.println("============初始化============");
        //ProducerType.MULTI 支持多事件发布
        disruptor = new Disruptor<MessageEvent>(new EventFactory<MessageEvent>() {
            @Override
            public MessageEvent newInstance() {
                return new MessageEvent();
            }
        }, 1024 * 2 , Executors.newFixedThreadPool(threadPoolSize) , ProducerType.MULTI ,new YieldingWaitStrategy());
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
        WorkHandler<MessageEvent> workHandler = new WorkHandler<MessageEvent>() {
            @Override
            public void onEvent(MessageEvent messageEvent) throws Exception {
                System.out.println("=============message=============");
            }
        };
        WorkHandler[] workHandlers = new WorkHandler[threadPoolSize];
        for(int i = 0 ; i < threadPoolSize ; i++){
            workHandlers[i] = workHandler;
        }
        disruptor.handleEventsWithWorkerPool(workHandlers);
        //启动
        disruptor.start();
        System.out.println("============初始化结束============");
    }

    /***
     * 容器销毁事件
     */
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
