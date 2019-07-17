package com.xzy.springbootcommondemo.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xuzhiyong
 * @createDate 2019-07-12-23:01
 */
public class CommandHelloWorldTest {

    @Test
    public void testSync(){
        HystrixCommandGroupKey hystrixCommandGroupKey = HystrixCommandGroupKey.Factory.asKey("ExampleGroup");
        CommandHelloWorld command = new CommandHelloWorld(hystrixCommandGroupKey,"World");
        String result = command.execute();
        assertEquals("Hello, World", result);
    }
}
