package com.alibaba.dubbo.performance.demo.agent.transport;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client.ThreadBoundClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 徐靖峰
 * Date 2018-06-02
 */
public final class ThreadBoundClientHolder {
    private ThreadBoundClientHolder(){
    }

    private static Map<String,ThreadBoundClient> threadBoundClientMap = new HashMap<>(8);

    public static void put(String eventLoopName,ThreadBoundClient threadBoundClient){
        threadBoundClientMap.put(eventLoopName, threadBoundClient);
    }

    public static ThreadBoundClient get(String eventLoopName){
        return threadBoundClientMap.get(eventLoopName);
    }

}
