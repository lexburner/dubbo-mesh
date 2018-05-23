package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 * <p>
 * 存放 consumer-agent 请求之后响应的future
 */
public class ConsumerAgentResponseFutureHolder {
    private static ConcurrentHashMap<Long, RpcCallbackFuture<AgentResponse>> processingRpc = new ConcurrentHashMap<>();

    public static void put(long requestId, RpcCallbackFuture<AgentResponse> rpcCallbackFuture) {
        processingRpc.put(requestId, rpcCallbackFuture);
    }

    public static RpcCallbackFuture<AgentResponse> get(long requestId) {
        return processingRpc.get(requestId);
    }

    public static void remove(long requestId) {
        processingRpc.remove(requestId);
    }

//    private static ConcurrentHashMap<Long, DeferredResult<ResponseEntity>> processingRpc = new ConcurrentHashMap<>();
//
//    public static void put(long requestId, DeferredResult<ResponseEntity> rpcCallbackFuture) {
//        processingRpc.put(requestId, rpcCallbackFuture);
//    }
//
//    public static DeferredResult<ResponseEntity> get(long requestId) {
//        return processingRpc.get(requestId);
//    }
//
//    public static void remove(long requestId) {
//        processingRpc.remove(requestId);
//    }
}
