package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Deprecated
public class RpcFuture implements Future<Object> {
    private CountDownLatch latch = new CountDownLatch(1);

    private DubboRpcResponse response;

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
            return response.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean b = latch.await(timeout, unit);
        return response.getBytes();
    }

    public void done(DubboRpcResponse response) {
        this.response = response;
        latch.countDown();
    }
}
