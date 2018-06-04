package com.alibaba.dubbo.performance.demo.agent.rpc;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public class RpcCallbackFuture<T> {

    private Channel channel;

    private T response;

    public T getResponse() {
        return response;
    }

    private List<FutureListener<T>> listeners;
    private Object lock = new Object();
    protected volatile FutureState state = FutureState.DOING;

    private boolean isDoing() {
        return state.isDoingState();
    }

    public void addListener(FutureListener<T> listener) {
        boolean notifyNow = false;
        synchronized (lock) {
            if (!isDoing()) {
                notifyNow = true;
            } else {
                if (listeners == null) {
                    listeners = new ArrayList<>(1);
                }
                listeners.add(listener);
            }
        }
        if (notifyNow) {
            notifyListener(listener);
        }
    }

    private void notifyListeners() {
        if (listeners != null) {
            for (FutureListener<T> listener : listeners) {
                notifyListener(listener);
            }
        }
    }

    private void notifyListener(FutureListener<T> listener) {
        listener.operationComplete(this);
    }

    public void done(T response) {
        this.response = response;
        this.done();
    }

    protected boolean done() {
        synchronized (lock) {
            if (!isDoing()) {
                return false;
            }
            state = FutureState.DONE;
        }
        notifyListeners();
        return true;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
