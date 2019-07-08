package com.xzy.springbootredisdemo.controller;

import com.google.common.collect.Maps;
import com.xzy.springbootredisdemo.annotation.RateLimiter;
import com.xzy.springbootredisdemo.util.ResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class TestController {
    private static final String MESSAGE = "{\"code\":\"400\",\"msg\":\"FAIL\",\"desc\":\"触发限流\"}";

    @RequestMapping("/testone")
    @RateLimiter(key = "ratelimit:testOne", limit = 5, expire = 10, message = MESSAGE, isLimiterIp = true)
    public String testOne(HttpServletRequest request) {
        return "正常请求";
    }

    @RequestMapping("/testtwo")
    @RateLimiter(key = "ratelimit:testTwo", limit = 5, expire = 10, isLimiterIp = false)
    public Map testTow(HttpServletRequest request){
        Map<String,String> resultMap = Maps.newHashMap();
        resultMap.put("code", "1");
        resultMap.put("message" , "success");
        return resultMap;
    }

    @RequestMapping("/testthree")
    @RateLimiter(key = "ratelimit:testThree", limit = 5, expire = 10, isLimiterIp = false)
    public ResultVo testThree(HttpServletRequest request){
        return ResultVo.getSuccessResultVo();
    }
}
