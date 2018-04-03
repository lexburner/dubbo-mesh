package com.alibaba.dubbo.performance.demo.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloService implements IHelloService {

    private Logger logger = LoggerFactory.getLogger(HelloService.class);

    @Override
    public int hash(String str) {
        int hashCode = str.hashCode();
        logger.info("hash(): " + str + " => " + hashCode);
        return hashCode;
    }
}
