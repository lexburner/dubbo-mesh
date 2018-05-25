package com.alibaba.dubbo.performance.demo.agent;

import org.apache.commons.lang3.RandomStringUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class SimpleBenchmark {
    private static AsyncHttpClient asyncHttpClient = org.asynchttpclient.Dsl.asyncHttpClient();
    private static Random r = new Random(1);

    public static void main(String[] args) throws Exception {

        AtomicInteger responsePerSecond = new AtomicInteger();
        AtomicLong responseTimePerSecond = new AtomicLong();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            if (responsePerSecond.get() != 0) {
                System.out.println("qps: " + responsePerSecond.get() / 5 + ", avg time: " + responseTimePerSecond.get
                        () /
                        responsePerSecond.get());
            }
            responsePerSecond.set(0);
            responseTimePerSecond.set(0);

        }, 0, 5, TimeUnit.SECONDS);

        String url = "http://127.0.0.1:20000";


        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String str = RandomStringUtils.random(r.nextInt(1024), true, true);

            org.asynchttpclient.Request request = org.asynchttpclient.Dsl.post(url)
                    .addFormParam("interface", "com.alibaba.dubbo.performance.demo.provider.IHelloService")
                    .addFormParam("method", "hash")
                    .addFormParam("parameterTypesString", "Ljava/lang/String;")
                    .addFormParam("parameter", str)
                    .build();

            long beginTime = System.currentTimeMillis();
            ListenableFuture<Response> responseFuture = asyncHttpClient.executeRequest(request);

            Runnable callback = () -> {
                try {
                    String value = responseFuture.get().getResponseBody();
                    if (String.valueOf(str.hashCode()).equals(value)) {
                        responseTimePerSecond.addAndGet(System.currentTimeMillis() - beginTime);
                        responsePerSecond.getAndIncrement();
                    } else {
                        System.err.println("error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            responseFuture.addListener(callback, null);
        }, 1000000, 300, TimeUnit.MICROSECONDS);

    }

}
