package com.alibaba.dubbo.performance.demo.agent.util;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QpsTrack {
    private static AtomicInteger event = new AtomicInteger();

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            if (event.get() != 0) {
                System.out.println("qps: " + event.get() / 5);
            }
            event.set(0);

        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void track() {
        event.getAndIncrement();
    }
}
