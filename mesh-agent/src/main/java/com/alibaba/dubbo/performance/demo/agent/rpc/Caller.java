package com.alibaba.dubbo.performance.demo.agent.rpc;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public interface Caller<T> {

    RpcCallbackFuture<T> asyncCall(Request request);

}
