package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.loadbalance.RoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentNettyClient {

    Logger logger = LoggerFactory.getLogger(ConsumerAgentNettyClient.class);

    private AgentClientConnecManager connectManager;
    RoundRobinLoadBalance loadBalance = new RoundRobinLoadBalance();
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private Object lock = new Object();

    public ConsumerAgentNettyClient() {
        this.connectManager = new AgentClientConnecManager();
        logger.info("ConsumerAgentNettyClient构造中...");
    }

    public RpcCallbackFuture<AgentResponse> invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        if (null == loadBalance.getEndpoints()) {
            synchronized (lock) {
                if (null == loadBalance.getEndpoints()) {
                    loadBalance.setEndpoints(registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService"));
                }
            }
        }
        // 简单的负载均衡，随机取一个
        Endpoint endpoint = loadBalance.select(null);
        Channel channel = connectManager.getChannel(endpoint);
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setInterfaceName(interfaceName);
        agentRequest.setMethod(method);
        agentRequest.setParameterTypesString(parameterTypesString);
        agentRequest.setParameter(parameter);
        channel.writeAndFlush(agentRequest);
        RpcCallbackFuture<AgentResponse> future = new RpcCallbackFuture<>();
        //TODO
        ConsumerAgentResponseFutureHolder.put(agentRequest.getId(), future);
        return future;
    }

}
