package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.ProviderAgentServer;
import com.alibaba.dubbo.performance.demo.agent.dubbo.consumer.ConsumerAgentHttpServer;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class AgentApp {

    static final Logger logger = LoggerFactory.getLogger(AgentApp.class);

    // agent会作为sidecar，部署在每一个Provider和Consumer机器上
    // 在Provider端启动agent时，添加JVM参数
    // -Dtype=provider -Dserver.port=30000 -Ddubbo.protocol.port=20880 -Detcd.url=http://localhost:2379 -Dlb.weight=4
    // 在Consumer端启动agent时，添加JVM参数
    // -Dtype=consumer -Dserver.port=20000 -Detcd.url=http://localhost:2379
    // 添加日志保存目录: -Dlogs.dir=/path/to/your/logs/dir。请安装自己的环境来设置日志目录。

    public static void main(String[] args) {
        SpringApplication.run(AgentApp.class, args);

        String type = System.getProperty("type");   // 获取type参数
        if ("provider".equals(type)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new ProviderAgentServer().startServer();
                }
            }).start();
        }
        if ("consumer".equals(type)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new ConsumerAgentHttpServer().startServer();
                }
            }).start();
        }
//        if ("consumer".equals(type)) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            OkHttpClient httpClient = new OkHttpClient.Builder().build();
//            try {
//                int port = Integer.parseInt(System.getProperty("server.port"));
//                final String url = "http://" + IpHelper.getHostIp() + ":" + "8087/invoke";
//                Random r = new Random(1);
//                final AtomicInteger count = new AtomicInteger(0);
//                int cnt = 1000;
//                CountDownLatch countDownLatch = new CountDownLatch(cnt);
//                ExecutorService executorService = Executors.newFixedThreadPool(128);
//                long start = System.currentTimeMillis();
//                for (int i = 0; i < cnt; i++) {
//                    executorService.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            RequestBody requestBody = new FormBody.Builder()
//                                    .add("interface", "com.alibaba.dubbo.performance.demo.provider.IHelloService")
//                                    .add("method", "hash")
//                                    .add("parameterTypesString", "Ljava/lang/String;")
//                                    .add("parameter", RandomStringUtils.random(r.nextInt(1024), true, true))
//                                    .build();
//
//                            Request request = new Request.Builder()
//                                    .url(url)
//                                    .post(requestBody)
//                                    .build();
//                            try (Response response = httpClient.newCall(request).execute()) {
//                            } catch (IOException e) {
//                                logger.error("压测请求返回结果异常", e);
//                                count.addAndGet(1);
//                            } finally {
//                                countDownLatch.countDown();
//                            }
//                        }
//                    });
//                }
//                countDownLatch.await();
//                executorService.shutdown();
//                System.out.println(count.get());
//                System.out.println("total cost " + (System.currentTimeMillis() - start) + " ms");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }



    }


}
