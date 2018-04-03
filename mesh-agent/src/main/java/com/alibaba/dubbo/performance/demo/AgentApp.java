package com.alibaba.dubbo.performance.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentApp {
    public static void main(String[] args) throws Exception {
        // 启动一个http server，用于监听consumer端的请求
         //new HttpServer(8088).run();
        SpringApplication.run(AgentApp.class,args);

//        RpcClient rpcClient = new RpcClient();
//        while (true) {
//            System.out.println(rpcClient.hello("leo"));
//            Thread.sleep(5 * 1000);
//        }
    }
}
