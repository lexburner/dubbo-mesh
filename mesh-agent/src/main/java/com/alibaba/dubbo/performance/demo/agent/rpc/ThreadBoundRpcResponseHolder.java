package com.alibaba.dubbo.performance.demo.agent.rpc;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadBoundRpcResponseHolder {

    public static ThreadLocal<HashMap<Long, RpcCallbackFuture>> futureMapHolder = ThreadLocal.withInitial(HashMap::new);

    public static void put(Long requestId, RpcCallbackFuture rpcFuture) {
        futureMapHolder.get().put(requestId, rpcFuture);
    }

    public static RpcCallbackFuture get(Long requestId) {
        return futureMapHolder.get().get(requestId);
    }

    public static void remove(Long requestId) {
        futureMapHolder.get().remove(requestId);
    }
}
