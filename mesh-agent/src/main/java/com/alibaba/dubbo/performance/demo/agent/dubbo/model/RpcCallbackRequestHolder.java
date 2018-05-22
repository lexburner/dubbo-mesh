package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public class RpcCallbackRequestHolder {

    // key: requestId     value: RpcCallbackFuture
    private static ConcurrentHashMap<String, RpcCallbackFuture> processingRpc = new ConcurrentHashMap<>();

    public static void put(String requestId, RpcCallbackFuture rpcFuture) {
        processingRpc.put(requestId, rpcFuture);
    }

    public static RpcCallbackFuture get(String requestId) {
        return processingRpc.get(requestId);
    }

    public static void remove(String requestId) {
        processingRpc.remove(requestId);
    }

}
