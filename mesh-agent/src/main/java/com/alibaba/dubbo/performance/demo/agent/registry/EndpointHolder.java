package com.alibaba.dubbo.performance.demo.agent.registry;

import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.WeightRoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-31
 */
public final class EndpointHolder {
    private final static Logger logger = LoggerFactory.getLogger(EndpointHolder.class);

    private EndpointHolder(){
    }

    private static List<Endpoint> endpoints;

    public static List<Endpoint> getEndpoints(){
        if(endpoints == null){
            synchronized (EndpointHolder.class){
                if(endpoints == null){
                    IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
                    List<Endpoint> remoteEndpoints = null;
                    try {
                        remoteEndpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    } catch (Exception e) {
                        logger.error("IHelloService接口访问etcd失败",e);
                        throw new RuntimeException(e);
                    }
                    return remoteEndpoints;
                }
            }
        }
        return endpoints;
    }

    public static void onRefresh(){
        synchronized (EndpointHolder.class){
            IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
            List<Endpoint> remoteEndpoints = null;
            try {
                remoteEndpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
            } catch (Exception e) {
                logger.error("IHelloService接口访问etcd失败",e);
                throw new RuntimeException(e);
            }
            endpoints = remoteEndpoints;
        }
    }

}
