package com.alibaba.dubbo.performance.demo.agent.transport;

import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 徐靖峰
 * Date 2018-06-05
 */
public class RateLimiter {
    public static Map<Endpoint,AtomicInteger> endpointAtomicIntegerMap = new HashMap<>(3);
}
