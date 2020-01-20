package com.xzy.datasourcecommon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {



    @RequestMapping("/hello")
    public String hello(HttpServletRequest request){
        return "hello";
    }
}
