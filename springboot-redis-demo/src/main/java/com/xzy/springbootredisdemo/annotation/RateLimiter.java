package com.xzy.springbootredisdemo.annotation;

import java.lang.annotation.*;

/***
 * 限流注解
 * key--表示限流模块名，指定该值用于区分不同应用，不同场景，推荐格式为：应用名:模块名:ip:接口名:方法名
 * limit--表示单位时间允许通过的请求数
 * expire--incr的值的过期时间，业务中表示限流的单位时间。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    /**
     * 限流key
     * @return
     */
    String key() default "rate:limiter";

    /**
     * 单位时间限制通过请求数
     * @return
     */
    long limit() default 10;

    /**
     * 过期时间，单位秒
     * @return
     */
    long expire() default 1;

    String message() default "限制访问";

    /***
     * 是否精确到ip控制
     * * @return
     */
    boolean isLimiterIp() default false;
}
