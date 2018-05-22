package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public interface FutureListener<T> {

    void operationComplete(RpcCallbackFuture<T> future);

}
