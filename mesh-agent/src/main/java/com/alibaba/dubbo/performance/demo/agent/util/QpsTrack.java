package com.alibaba.dubbo.performance.demo.agent.util;

import com.alibaba.dubbo.performance.demo.agent.AgentApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QpsTrack {
    private final String methodName;
    private AtomicInteger count;
    static final Logger logger = LoggerFactory.getLogger(AgentApp.class);

    public QpsTrack(String methodName) {
        this.methodName = methodName;
        this.count = new AtomicInteger();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (count.get() != 0) {
                logger.info("[" + methodName + "] >>>" + " qps: " + count.get() / 5);
            }
            count.set(0);

        }, 0, 5, TimeUnit.SECONDS);
    }

    static {
    }

    public void track() {
        count.getAndIncrement();
    }
}
