package com.alibaba.dubbo.performance.demo.agent.loadbalance;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-19
 */
public class RoundRobinLoadBalance implements LoadBalance {

    private List<Endpoint> endpoints;

    /**
     * 为考虑比赛性能不考虑溢出
     */
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Endpoint select(RpcInvocation invocation) {
        Endpoint endpoint = endpoints.get(atomicInteger.incrementAndGet() % endpoints.size());
        return endpoint;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
