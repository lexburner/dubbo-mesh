package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentCallbackRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.channel.Channel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

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
