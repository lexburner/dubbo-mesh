package com.alibaba.dubbo.performance.demo123.provider;


public class HelloService implements IHelloService {
    @Override
    public String hello() {
        System.out.println("Hello, leo");
        return "Hello, leo";
    }

    @Override
    public String hello(String name) {
        System.out.println("Hello " + name);
        return "Hello, " + name;
    }
}
