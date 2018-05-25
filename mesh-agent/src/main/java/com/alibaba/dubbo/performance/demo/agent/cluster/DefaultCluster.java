package com.alibaba.dubbo.performance.demo.agent.cluster;

import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.WeightRoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.rpc.Request;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public class DefaultCluster<T> implements Cluster<T> {

    private LoadBalance loadBalance;

    private List<Client> clients;

    private volatile boolean available = false;

    public DefaultCluster(List<Client> clients){
        this.loadBalance = new WeightRoundRobinLoadBalance();
        this.clients = clients;
        this.init();
    }

    public DefaultCluster(List<Client> clients,LoadBalance loadBalance){
        this.loadBalance = loadBalance;
        this.clients = clients;
        this.init();
    }

    @Override
    public RpcCallbackFuture<T> asyncCall(Request request) {
        if(available){
            Client client = loadBalance.select(request);
            return client.asyncCall(request);
        }else{
            throw new RuntimeException("集群未启动");
        }
    }

    @Override
    public void init() {
        loadBalance.onRefresh(clients);
        this.available = true;
    }

    @Override
    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public void onRefresh(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public List<Client> getClients() {
        return clients;
    }

    @Override
    public LoadBalance getLoadBalance() {
        return loadBalance;
    }


}
