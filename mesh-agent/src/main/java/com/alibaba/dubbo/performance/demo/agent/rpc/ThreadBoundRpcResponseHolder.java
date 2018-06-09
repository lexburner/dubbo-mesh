package com.alibaba.dubbo.performance.demo.agent.rpc;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;

public class ThreadBoundRpcResponseHolder {


    public static FastThreadLocal<LongObjectHashMap<RpcCallbackFuture>> futureMapHolder = new FastThreadLocal<LongObjectHashMap<RpcCallbackFuture>>() {
        @Override
        protected LongObjectHashMap<RpcCallbackFuture> initialValue() throws Exception {
            return new LongObjectHashMap<>();
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
