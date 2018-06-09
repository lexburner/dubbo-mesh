package com.alibaba.dubbo.performance.demo.agent.rpc;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;

public class ThreadBoundRpcResponseHolder {

    //    public static ThreadLocal<HashMap<Long, RpcCallbackFuture>> futureMapHolder = ThreadLocal.withInitial(HashMap::new);
    public static FastThreadLocal<HashMap<Long, RpcCallbackFuture>> futureMapHolder = new FastThreadLocal<HashMap<Long, RpcCallbackFuture>>() {
        @Override
        protected HashMap<Long, RpcCallbackFuture> initialValue() throws Exception {
            return new HashMap<>();
        }
    };

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
