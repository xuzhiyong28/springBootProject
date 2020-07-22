package com.xzy.springbootredisdemo.aop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xzy.springbootredisdemo.annotation.RateLimiter;
import com.xzy.springbootredisdemo.util.IpUtil;
import com.xzy.springbootredisdemo.util.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

//aop支持
@Aspect
@Component
public class RateLimterHandler {
    private static final Logger logger = LoggerFactory.getLogger(RateLimterHandler.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private DefaultRedisScript<Long> redisScript;

    //@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法
    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("rateLimter.lua")));
        logger.info("RateLimterHandler[分布式限流处理器]脚本加载完成");
    }

    //定义切点方法为注解RateLimiter名称为rateLimiter
    @Pointcut("@annotation(com.xzy.springbootredisdemo.annotation.RateLimiter)")
    public void rateLimiter() {
    }

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RateLimiter rateLimiter) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("注释应该配置在方法上");
        }
        //获取方法参数
        HttpServletRequest request = getArgsRequest(proceedingJoinPoint);
        //获取注解参数
        // 限流模块key
        String limitKey = rateLimiter.key();
        Preconditions.checkNotNull(limitKey); //判断是否为空
        //是否根据ip进行限流
        boolean isLimiterIp = rateLimiter.isLimiterIp();
        if (isLimiterIp && request != null) {
            if (request != null) {
                String ip = IpUtil.getIpAddr(request);
                limitKey = limitKey + ":" + ip;
            }
        }
        //限流阀值
        long limitTimes = rateLimiter.limit();
        // 限流超时时间
        long expireTime = rateLimiter.expire();
        if (logger.isDebugEnabled()) {
            logger.debug("RateLimterHandler[分布式限流处理器]参数值为-limitTimes={},limitTimeout={}", limitTimes, expireTime);
        }
        //限流提示语
        String message = rateLimiter.message();
        //执行lua脚本
        List<String> keyList = Lists.newArrayList();
        keyList.add(limitKey);
        Long result = (Long) redisTemplate.execute(redisScript, keyList, expireTime, limitTimes);
        if (result == 0) {
            Type type = getMethodReturnType(proceedingJoinPoint);
            logger.info("由于超过单位时间=" + expireTime + "-允许的请求次数=" + limitTimes + "[触发限流]");
            return limitErrorReturn(type, message);
        }
        return proceedingJoinPoint.proceed();
    }


    public Object limitErrorReturn(Type type, String msg) {
        switch (type.getTypeName()) {
            case "java.lang.String":
                return msg;
            case "java.util.Map":
                Map<String, String> resultMap = Maps.newHashMap();
                resultMap.put("code", "-1");
                resultMap.put("message", msg);
                return resultMap;
            case "com.xzy.springbootredisdemo.util.ResultVo":
                return ResultVo.getErrorResultVo(msg);
            default:
                return msg;
        }
    }


    public HttpServletRequest getArgsRequest(ProceedingJoinPoint proceedingJoinPoint) throws NoSuchMethodException {
        //获取方法参数
        Object[] args = proceedingJoinPoint.getArgs();
        HttpServletRequest request = null;
        for (Object o : args) {
            if (o instanceof HttpServletRequest) {
                request = (HttpServletRequest) o;
            }
        }
        return request;
    }

    /***
     * 获取返回值类型
     * @param proceedingJoinPoint
     * @return
     * @throws NoSuchMethodException
     */
    public Type getMethodReturnType(ProceedingJoinPoint proceedingJoinPoint) throws NoSuchMethodException {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = proceedingJoinPoint.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return currentMethod.getAnnotatedReturnType().getType();
    }

}
