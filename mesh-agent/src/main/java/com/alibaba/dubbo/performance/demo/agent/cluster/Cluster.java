package com.alibaba.dubbo.performance.demo.agent.cluster;

import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.rpc.Caller;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public interface Cluster<T> extends Caller<T> {

    void init();

    void setLoadBalance(LoadBalance loadBalance);

    void onRefresh(List<Client> clients);

    List<Client> getClients();

    LoadBalance getLoadBalance();
}
