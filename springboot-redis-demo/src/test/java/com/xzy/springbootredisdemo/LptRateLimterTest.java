package com.xzy.springbootredisdemo;

import com.xzy.springbootredisdemo.lptRateLimter.RedLimiterImpl;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LptRateLimterTest {

    @Test
    public void test0(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost");
        RedLimiterImpl rateLimiter = RedLimiterImpl.create("limit001", 1, jedisPool, true);
        ExecutorService pool = Executors.newFixedThreadPool(500);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            pool.execute(() -> {
                double acquire = rateLimiter.acquire(10);
                System.out.println(index + " \t" + acquire + " \t" + new Date());
            });
        }
    }
}
