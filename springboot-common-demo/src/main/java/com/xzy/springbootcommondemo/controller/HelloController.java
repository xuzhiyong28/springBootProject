package com.xzy.springbootcommondemo.controller;

import com.xzy.springbootcommondemo.constant.JdbcBeanConstant;
import com.xzy.springbootcommondemo.constant.StudentConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @Value("${common.test.name}")
    private String commonTestName;
    @Value("${common.test.age}")
    private String commonTestAge;

    @Autowired
    private StudentConstant studentConstant;

    @Autowired
    private JdbcBeanConstant jdbcBeanConstant;


    @RequestMapping("/hello")
    public String hello(HttpServletRequest request){
        return "hello" + commonTestAge + commonTestName + studentConstant.getName() + jdbcBeanConstant.getDriverclassname();
    }
}
