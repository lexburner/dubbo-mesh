package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-21
 */
public class ProviderAgentRpcResponseFutureHolder {

    // key: requestId     value: RpcCallbackFuture
    private static Map<String, RpcCallbackFuture<ProviderAgentRpcResponse>> processingRpc = new ConcurrentHashMap<>();

    public static void put(String requestId, RpcCallbackFuture<ProviderAgentRpcResponse> rpcFuture) {
        processingRpc.put(requestId, rpcFuture);
    }

    public static RpcCallbackFuture<ProviderAgentRpcResponse> get(String requestId) {
        return processingRpc.get(requestId);
    }

    public static void remove(String requestId) {
        processingRpc.remove(requestId);
    }


}
