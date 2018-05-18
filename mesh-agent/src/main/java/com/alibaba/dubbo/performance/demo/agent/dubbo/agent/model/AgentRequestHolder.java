package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentRequestHolder {
    private static ConcurrentHashMap<Long,AgentFuture> processingRpc = new ConcurrentHashMap<>();

    public static void put(long requestId,AgentFuture rpcFuture){
        processingRpc.put(requestId,rpcFuture);
    }

    public static AgentFuture get(long requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(long requestId){
        processingRpc.remove(requestId);
    }
}
