package com.alibaba.dubbo.performance.demo.agent.transport;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client.ConsumerAgentClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 徐靖峰
 * Date 2018-06-02
 */
public final class ThreadBoundClientHolder {
    private ThreadBoundClientHolder(){
    }

    private static Map<String,ConsumerAgentClient> threadBoundClientMap = new HashMap<>(8);

    public static void put(String eventLoopName,ConsumerAgentClient consumerAgentClient){
        threadBoundClientMap.put(eventLoopName, consumerAgentClient);
    }

    public static ConsumerAgentClient get(String eventLoopName){
        return threadBoundClientMap.get(eventLoopName);
    }

}
