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

    private static AtomicInteger responsePerSecond = new AtomicInteger();
    private static AtomicLong responseTimePerSecond = new AtomicLong();

    private static String url = "http://127.0.0.1:20000";

    public static void main(String[] args) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            if (responsePerSecond.get() != 0) {
                System.out.println("qps: " + responsePerSecond.get() / 5 + ", avg time: " + responseTimePerSecond.get
                        () /
                        responsePerSecond.get());
            }
            responsePerSecond.set(0);
            responseTimePerSecond.set(0);

        }, 0, 5, TimeUnit.SECONDS);


        for (int i = 0; i < 200; i++) {
            bench();
        }
    }

    private static void bench() {
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
                    bench();
                } else {
                    System.err.println("error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        responseFuture.addListener(callback, null);

    }

}
