package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AgentFuture implements Future<Object> {
    private CountDownLatch latch = new CountDownLatch(1);

    private AgentResponse response;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException {
        latch.await();
        try {
            return response.getValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        latch.await(timeout,unit);
        return response.getValue();
    }

    public void done(AgentResponse response){
        this.response = response;
        latch.countDown();
    }
}
