package com.alibaba.dubbo.performance.demo.agent.rpc;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public interface FutureListener<T> {

    /**
     * 回调时触发
     * @param future
     */
    void operationComplete(RpcCallbackFuture<T> future);

}
