package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.loadbalance.RoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

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

    public RpcCallbackFuture<ProviderAgentRpcResponse> invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        if (null == loadBalance.getEndpoints()) {
            synchronized (lock) {
                if (null == loadBalance.getEndpoints()) {
                    loadBalance.setEndpoints(registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService"));
                }
            }
        }
        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        ProviderAgentRpcRequest providerAgentRpcRequest = new ProviderAgentRpcRequest();
        providerAgentRpcRequest.setVersion("2.0.0");
        providerAgentRpcRequest.setTwoWay(true);
        providerAgentRpcRequest.setData(invocation);

        logger.info("requestId=" + providerAgentRpcRequest.getId());
        RpcCallbackFuture<ProviderAgentRpcResponse> rpcResponseRpcCallbackFuture = new RpcCallbackFuture<>();
        Channel channel = connectManager.getChannel(loadBalance.select(null));
        ConsumerAgentResponseFutureHolder.put(providerAgentRpcRequest.getId(), rpcResponseRpcCallbackFuture);
        channel.writeAndFlush(providerAgentRpcRequest);
        return rpcResponseRpcCallbackFuture;
    }

}
