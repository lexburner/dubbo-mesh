package com.alibaba.dubbo.performance.demo.agent.rpc;

import java.util.concurrent.ConcurrentHashMap;

public class RpcResponseHolder {

    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<Long, RpcCallbackFuture> processingRpc = new ConcurrentHashMap<>();

    public static void put(Long requestId, RpcCallbackFuture rpcFuture) {
        processingRpc.put(requestId, rpcFuture);
    }

    public static RpcCallbackFuture get(Long requestId) {
        return processingRpc.get(requestId);
    }

    public static void remove(Long requestId) {
        processingRpc.remove(requestId);
    }
}
