package com.alibaba.dubbo.performance.demo123.provider;


public class HelloServiceMock implements IHelloService {
    @Override
    public String hello() {
        return "Mock";
    }

    @Override
    public String hello(String name) {
        return "Mock " + name;
    }
}
