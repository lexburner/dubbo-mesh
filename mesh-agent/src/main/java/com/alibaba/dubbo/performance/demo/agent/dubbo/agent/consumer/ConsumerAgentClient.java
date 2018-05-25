package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.loadbalance.RoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author 徐靖峰
 * Date 2018-05-22
 */
public class ConsumerAgentClient {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentClient.class);

    private ConsumerAgentConnectionManager connectManager;
    private RoundRobinLoadBalance loadBalance = new RoundRobinLoadBalance();
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private Object lock = new Object();

    public ConsumerAgentClient() {
        this.connectManager = new ConsumerAgentConnectionManager();
        logger.info("ConsumerAgentNettyClient构造中...");
    }

    /**
     * consumerAgent发起请求的入口
     * @param interfaceName
     * @param method
     * @param parameterTypesString
     * @param parameter
     * @return
     * @throws Exception
     */
    public RpcCallbackFuture<DubboRpcResponse> invoke(String interfaceName, String method, String parameterTypesString, String parameter)
            throws Exception {
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

        DubboRpcRequest dubboRpcRequest = new DubboRpcRequest();
        dubboRpcRequest.setVersion("2.0.0");
        dubboRpcRequest.setTwoWay(true);
        dubboRpcRequest.setData(invocation);
//        logger.info("requestId=" + dubboRpcRequest.getId());
        RpcCallbackFuture<DubboRpcResponse> rpcResponseRpcCallbackFuture = new RpcCallbackFuture<>();
        Channel channel = connectManager.getChannel(loadBalance.select(null));
        ConsumerAgentResponseHolder.put(dubboRpcRequest.getId(), rpcResponseRpcCallbackFuture);
        channel.writeAndFlush(dubboRpcRequest);
        return rpcResponseRpcCallbackFuture;
    }

}
