package com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance;

import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-19
 */
public interface LoadBalance {

    Endpoint select();

    void onRefresh(List<Endpoint> endpoints);

}
