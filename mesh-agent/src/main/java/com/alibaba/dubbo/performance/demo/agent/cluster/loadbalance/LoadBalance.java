package com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance;

import com.alibaba.dubbo.performance.demo.agent.rpc.Request;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-19
 */
public interface LoadBalance {

    Client select(Request request);

    void onRefresh(List<Client> clients);

}
