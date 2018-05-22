package com.alibaba.dubbo.performance.demo.agent.dubbo.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client.ConsumerAgentNettyClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerClient {

    public ConsumerClient() {
        System.out.println("==>ConsumerClient构造...");
    }

    Logger logger = LoggerFactory.getLogger(ConsumerClient.class);

    ConsumerAgentNettyClient consumerAgentNettyClient = new ConsumerAgentNettyClient();

    public RpcCallbackFuture<AgentResponse> invoke(String interfaceName, String method, String parameterTypesString, String parameter) {
        try {
            RpcCallbackFuture<AgentResponse> rpcCallbackFuture = consumerAgentNettyClient.invoke(interfaceName, method, parameterTypesString, parameter);
            return rpcCallbackFuture;
        } catch (Exception e) {
            logger.error("ConsumerClient请求错误", e);
            throw new RuntimeException(e);
        }
    }

}
