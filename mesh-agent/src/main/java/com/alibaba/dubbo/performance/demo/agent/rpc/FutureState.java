package com.alibaba.dubbo.performance.demo.agent.rpc;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public enum FutureState {
    /**
     * the task is doing
     **/
    DOING(0),
    /**
     * the task is done
     **/
    DONE(1),
    /**
     * ths task is cancelled
     **/
    CANCELLED(2);

    private final int value;

    FutureState(int value) {
        this.value = value;
    }

    public boolean isCancelledState() {
        return this == CANCELLED;
    }

    public boolean isDoneState() {
        return this == DONE;
    }

    public boolean isDoingState() {
        return this == DOING;
    }
}
