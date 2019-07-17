package com.xzy.springbootcommondemo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * @author xuzhiyong
 * @createDate 2019-07-12-23:03
 */
public class CommandHelloWorld extends HystrixCommand<String> {

    private String name;
    public CommandHelloWorld(HystrixCommandGroupKey group, String name) {
        super(group);
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        if ("Alice".equals(name)) {
            throw new RuntimeException("出错了");
        }
        return "Hello, " + name;
    }

    @Override
    protected String getFallback() {
        return "Failure, " + name;
    }
}
