package com.alibaba.dubbo.performance.demo.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentApp {
    // agent会作为sidecar，部署在每一个Provider和Consumer机器上
    // 在Provider端启动agent时，添加JVM参数-Dtype=provider -Dserver.port=30000
    // 在Consumer端启动agent时，添加JVM参数-Dtype=consumer -Dserver.port=20000

    public static void main(String[] args) {
        SpringApplication.run(AgentApp.class,args);
    }
}
