package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 * <p>
 * 存放 consumer-agent 请求之后响应的future
 */
public final class DubboResponseHolder {

    private DubboResponseHolder() {
    }

    private static Logger logger = LoggerFactory.getLogger(DubboResponseHolder.class);

    private static Map<Long, RpcCallbackFuture<DubboRpcResponse>> processingRpc = new ConcurrentHashMap<>();

    /**
     * put
     *
     * @param requestId
     * @param rpcCallbackFuture
     */
    public static void put(long requestId, RpcCallbackFuture<DubboRpcResponse> rpcCallbackFuture) {
        processingRpc.put(requestId, rpcCallbackFuture);
//        logger.info("{} put {} into concurrentHashMap",Thread.currentThread().getName(),requestId);
    }

    /**
     * get
     *
     * @param requestId
     * @return
     */
    public static RpcCallbackFuture<DubboRpcResponse> get(long requestId) {
//        logger.info("{} get {} from concurrentHashMap",Thread.currentThread().getName(),requestId);
        return processingRpc.get(requestId);
    }

    /**
     * remove
     *
     * @param requestId
     */
    public static void remove(long requestId) {
//        logger.info("{} remove {} from concurrentHashMap",Thread.currentThread().getName(),requestId);
        processingRpc.remove(requestId);
    }

}
