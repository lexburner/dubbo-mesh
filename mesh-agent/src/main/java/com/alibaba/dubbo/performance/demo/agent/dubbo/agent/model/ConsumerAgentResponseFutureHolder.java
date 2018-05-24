package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 * <p>
 * 存放 consumer-agent 请求之后响应的future
 */
public class ConsumerAgentResponseFutureHolder {
//    private static ConcurrentHashMap<Long, RpcCallbackFuture<ProviderAgentRpcResponse>> processingRpc = new ConcurrentHashMap<>();
//
//    public static void put(long requestId, RpcCallbackFuture<ProviderAgentRpcResponse> rpcCallbackFuture) {
//        processingRpc.put(requestId, rpcCallbackFuture);
//    }
//
//    public static RpcCallbackFuture<ProviderAgentRpcResponse> get(long requestId) {
//        return processingRpc.get(requestId);
//    }
//
//    public static void remove(long requestId) {
//        processingRpc.remove(requestId);
//    }

    private static Map<Long, DeferredResult<ResponseEntity>> processingRpc = new ConcurrentHashMap<>();

    public static void put(long requestId, DeferredResult<ResponseEntity> rpcCallbackFuture) {
        processingRpc.put(requestId, rpcCallbackFuture);
    }

    public static DeferredResult<ResponseEntity> get(long requestId) {
        return processingRpc.get(requestId);
    }

    public static void remove(long requestId) {
        processingRpc.remove(requestId);
    }

//    private static Map<Long, RpcFuture> processingRpc = new ConcurrentHashMap<>();
//
//    public static void put(long requestId, RpcFuture rpcCallbackFuture) {
//        processingRpc.put(requestId, rpcCallbackFuture);
//    }
//
//    public static RpcFuture get(long requestId) {
//        return processingRpc.get(requestId);
//    }
//
//    public static void remove(long requestId) {
//        processingRpc.remove(requestId);
//    }
}
