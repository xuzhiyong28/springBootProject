package com.xzy.springbootredisdemo;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class OtherTest {

    @Test
    public void test0(){
        RateLimiter limiter = RateLimiter.create(5);
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
    }

    @Test
    public void test1() throws InterruptedException {
        RateLimiter limiter = RateLimiter.create(3);
        TimeUnit.SECONDS.sleep(1);
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
    }


    @Test
    public void test2() throws InterruptedException {
        //limiter.tryAcquire(int permits,long timeout, TimeUnit unit) 表示一次性获取permits个令牌，如果在timeout秒内能获取得到令牌则阻塞，直到获取到，如果不行就直接返回false
        //RateLimiter.create(5) 表示桶容量为5且每秒新增5个令牌，即每隔200毫秒新增一个令牌；
        RateLimiter limiter = RateLimiter.create(5);
        //因为允许突发，第一次取100个，那么接下去20秒的请求都会被拒绝
        System.out.println(limiter.tryAcquire(100));
        long start = System.currentTimeMillis();
        System.out.println(limiter.tryAcquire(5,60,TimeUnit.SECONDS));
        System.out.println((System.currentTimeMillis() - start) + " ms");
        //到这一步已经桶里面已经没有令牌了,所以接下去返回false
        System.out.println(limiter.tryAcquire(1));
        TimeUnit.SECONDS.sleep(1);
        //等待1秒后，桶里新生成了5个令牌，所以接下去获取返回true
        System.out.println(limiter.tryAcquire(5));
    }

    @Test
    public void test3(){
        RateLimiter limiter = RateLimiter.create(5);
        System.out.println(limiter.acquire(200));
        long start = System.currentTimeMillis();
        System.out.println(limiter.acquire(1));
        System.out.println((System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void test4() throws InterruptedException {
        RateLimiter limiter = RateLimiter.create(5, 1000, TimeUnit.MILLISECONDS);
        for(int i = 0; i < 5;i++) {
            System.out.println(limiter.acquire());
        }
        Thread.sleep(1000L);
        for(int i = 0; i < 5;i++) {
            System.out.println(limiter.acquire());
        }
    }
}
