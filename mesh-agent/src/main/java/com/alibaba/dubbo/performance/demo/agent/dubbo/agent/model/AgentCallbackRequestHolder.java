package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentCallbackRequestHolder {
    private static ConcurrentHashMap<Long,DeferredResult<ResponseEntity>> processingRpc = new ConcurrentHashMap<>();

    public static void put(long requestId,DeferredResult<ResponseEntity> deferredResult){
        processingRpc.put(requestId,deferredResult);
    }

    public static DeferredResult<ResponseEntity> get(long requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(long requestId){
        processingRpc.remove(requestId);
    }
}
