package com.alibaba.dubbo.performance.demo123.provider;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/hello")
    public String hello(){
        return "Hello";
    }

    @RequestMapping(value = "/hello/{name}")
    public String hello(@PathVariable("name") String name){
        return "Hello " + name;
    }

}
