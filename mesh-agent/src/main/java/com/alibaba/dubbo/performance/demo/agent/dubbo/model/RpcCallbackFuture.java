package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public class RpcCallbackFuture {

    private RpcResponse response;

    public RpcResponse getResponse() {
        return response;
    }

    private List<FutureListener> listeners;
    private Object lock = new Object();
    protected volatile FutureState state = FutureState.DOING;

    private boolean isDoing() {
        return state.isDoingState();
    }

    public void addListener(FutureListener listener) {
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
            for (FutureListener listener : listeners) {
                notifyListener(listener);
            }
        }
    }

    private void notifyListener(FutureListener listener) {
        listener.operationComplete(this);
    }

    public void done(RpcResponse response){
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
}
