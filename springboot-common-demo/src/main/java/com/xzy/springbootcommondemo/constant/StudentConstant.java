package com.xzy.springbootcommondemo.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

//将配置文件class.student映射到Student.class
//记得在启动类上加@EnableConfigurationProperties({Student.class})
@ConfigurationProperties(prefix = "class.student")
public class StudentConstant {
    private String name;
    private int age;
    private Double grade;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
