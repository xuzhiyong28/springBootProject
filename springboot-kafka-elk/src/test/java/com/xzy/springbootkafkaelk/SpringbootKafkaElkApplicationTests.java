package com.xzy.springbootkafkaelk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SpringbootKafkaElkApplicationTests {

	@Autowired
	private KafkaTemplate<String,String> kafkaTemplate;


	@Test
	void contextLoads() {
		System.out.println(kafkaTemplate.getDefaultTopic());
	}

	@Test
	public void testSend() throws InterruptedException {
		ListenableFuture<SendResult<String, String>> future =  kafkaTemplate.send("topic20191019", "我是许志勇");
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onFailure(Throwable throwable) {
				System.out.println(throwable);
			}

			@Override
			public void onSuccess(SendResult<String, String> stringStringSendResult) {
				System.out.println("消息创建成功");
				System.out.println(stringStringSendResult);
			}
		});
		TimeUnit.SECONDS.sleep(10);
	}


	/***
	 * 批量发送消息
	 * @throws InterruptedException
	 */
	@Test
	public void testMutilSend() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		for(int i = 0 ; i < 100 ; i++){
			int finalI = i;
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					sendMess("topic20191019","我是许志勇_" + finalI);
				}
			});
		}
		executorService.awaitTermination(10,TimeUnit.SECONDS);
		executorService.shutdown();

	}

	/***
	 * 发送消息
	 * @param topic
	 * @param message
	 */
	public void sendMess(String topic , String message){
		ListenableFuture<SendResult<String, String>> future =  kafkaTemplate.send(topic, message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onFailure(Throwable throwable) {
				System.out.println(throwable);
			}

			@Override
			public void onSuccess(SendResult<String, String> stringStringSendResult) {
				System.out.println("消息创建成功");
				System.out.println(stringStringSendResult);
			}
		});
	}
}
