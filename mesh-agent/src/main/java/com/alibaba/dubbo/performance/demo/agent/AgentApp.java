package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server.ProviderAgentServer;
import com.alibaba.dubbo.performance.demo.agent.dubbo.consumer.ConsumerAgentHttpServer;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Random;

@SpringBootApplication
public class AgentApp {
    // agent会作为sidecar，部署在每一个Provider和Consumer机器上
    // 在Provider端启动agent时，添加JVM参数
    // -Dtype=provider -Dserver.port=30000 -Ddubbo.protocol.port=20880 -Detcd.url=http://localhost:2379
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
        if ("consumer".equals(type)) {
            OkHttpClient httpClient = new OkHttpClient();
            try {
                int port = Integer.parseInt(System.getProperty("server.port"))-1;
                final String url = "http://" + IpHelper.getHostIp() + ":" + port;
                Random r = new Random(1);
                for (int i = 0; i < 10; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("interface", "com.alibaba.dubbo.performance.demo.provider.IHelloService")
                                    .add("method", "hash")
                                    .add("parameterTypesString", "Ljava/lang/String;")
                                    .add("parameter", RandomStringUtils.random(r.nextInt(1024), true, true))
                                    .build();

                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();
                            try (Response response = httpClient.newCall(request).execute()) {
                                System.out.println(new String(response.body().bytes()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
