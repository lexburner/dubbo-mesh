package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.*;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server.AgentServerHandler;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.nio.charset.Charset;
import java.util.concurrent.Callable;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentAsyncClient {

    private AgentClientConnecManager connectManager;

    public AgentAsyncClient(){
        this.connectManager = new AgentClientConnecManager();
    }

    Logger logger = LoggerFactory.getLogger(AgentServerHandler.class);

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter, Endpoint endpoint) throws Exception {
        Channel channel = connectManager.getChannel(endpoint);
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setInterfaceName(interfaceName);
        agentRequest.setMethod(method);
        agentRequest.setParameterTypesString(parameterTypesString);
        agentRequest.setParameter(parameter);
        channel.writeAndFlush(agentRequest);

        DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>();
        AgentCallbackRequestHolder.put(agentRequest.getId(), deferredResult);

        return deferredResult;
    }
}
