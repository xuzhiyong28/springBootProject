package com.xzy.springbootredisdemo.lptRateLimter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.*;

public class RedLimiterImpl {
    private static final String STORED_PERMITS = "storedPermits";
    private static final String MAX_PERMITS = "maxPermits";
    private static final String STABLE_INTERVAL_MICROS = "stableIntervalMicros";
    private static final String NEXT_FREE_TICKET_MICROS = "nextFreeTicketMicros";

    private static final String SCRIPT_LUA = "redLimiterImpl.lua";
    private static final String SCRIPT = readScript();

    private static final ConcurrentMap<String, RedLimiterImpl> LIMITERS = new ConcurrentHashMap<>();

    private final String key;
    private final JedisPool jedisPool;
    private double qps;
    private String sha1;
    private volatile int batchSize = 100;
    private volatile long lastMillis = 0L;
    private volatile long batchInterval = 100L;

    private AtomicInteger qpsHolder = new AtomicInteger(0);

    private RedLimiterImpl(String key, double qps, JedisPool jedisPool, boolean setProperties) {
        this.key = key;
        this.qps = qps;
        this.jedisPool = jedisPool;
        if (jedisPool == null) {
            throw new RuntimeException("no redis client");
        }
        if (setProperties) {
            setProperties();
        }
        loadScriptSha1();
    }

    private void setProperties() {
        //初始化
        Map<String, String> limiter = new HashMap<>();
        limiter.put(STORED_PERMITS, Double.toString(qps));
        limiter.put(MAX_PERMITS, Double.toString(qps));
        limiter.put(STABLE_INTERVAL_MICROS, Double.toString(TimeUnit.SECONDS.toMicros(1L) / qps));
        limiter.put(NEXT_FREE_TICKET_MICROS, "0");

        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hmset(key, limiter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadScriptSha1() {
        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                this.sha1 = jedis.scriptLoad(SCRIPT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String readScript() {
        InputStream is = RedLimiterImpl.class.getClassLoader().getResourceAsStream(SCRIPT_LUA);
        Objects.requireNonNull(is);
        StringBuilder builder = new StringBuilder();
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
            }
        } catch (IOException e) {
            // will not reach here
        }
        return builder.toString();
    }


    public static RedLimiterImpl create(String key, double qps, JedisPool jedisPool) {
        return create(key, qps, jedisPool, false);
    }

    public static RedLimiterImpl create(String key, double qps, JedisPool jedisPool, boolean setProperties) {
        return LIMITERS.computeIfAbsent(key, k -> new RedLimiterImpl(k, qps, jedisPool, setProperties));
    }

    public static RedLimiterImpl create(String key, double qps) {
        return create(key, qps, false);
    }

    public static RedLimiterImpl create(String key, double qps,  boolean setProperties) {
        return LIMITERS.computeIfAbsent(key, k -> new RedLimiterImpl(k, qps, null, setProperties));
    }

    public void setRate(double qps) {
        this.qps = qps;
        setProperties();
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setBatchInterval(long batchInterval) {
        this.batchInterval = batchInterval;
    }

    public double acquire() {
        return acquire(1D);
    }

    public double acquireLazy(int batchQps) {
        qpsHolder.addAndGet(batchQps);
        long currentMillis = System.currentTimeMillis();
        if (qpsHolder.get() >= batchSize || (currentMillis - this.lastMillis) >= batchInterval) {
            int qps = qpsHolder.getAndSet(0);
            this.lastMillis = currentMillis;
            return acquire(qps);
        } else {
            return 0D;
        }
    }

    public double acquire(double qps) {
        long nowMicros = MILLISECONDS.toMicros(System.currentTimeMillis());
        long waitMicros = 0;
        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                waitMicros = (long) jedis.evalsha(sha1, 1, key, "acquire",
                        Double.toString(qps), Long.toString(nowMicros));
            }
        }
        double wait = 1.0 * waitMicros / SECONDS.toMicros(1L);
        if (waitMicros > 0) {
            sleepUninterruptibly(waitMicros, MICROSECONDS);
        }
        return wait;
    }

    public boolean tryAcquire() {
        return tryAcquire(1D, 0L, MICROSECONDS);
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return tryAcquire(1D, timeout, unit);
    }

    public boolean tryAcquire(double qps, long timeout, TimeUnit unit) {
        long nowMicros = MILLISECONDS.toMicros(System.currentTimeMillis());
        long timeoutMicros = unit.toMicros(timeout);
        long waitMicros = 0;
        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                waitMicros = (long) jedis.evalsha(sha1, 1, key, "tryAcquire",
                        Double.toString(qps), Long.toString(nowMicros), Long.toString(timeoutMicros));
            }
        }
        if (waitMicros < 0) {
            return false;
        }
        if (waitMicros > 0) {
            sleepUninterruptibly(waitMicros, MICROSECONDS);
        }
        return true;
    }

    // from Guava Uninterruptibles
    private static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(sleepFor);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    // TimeUnit.sleep() treats negative timeouts just like zero.
                    NANOSECONDS.sleep(remainingNanos);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
