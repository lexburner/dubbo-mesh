package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.channel.Channel;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentClient {

    private AgentClientConnecManager connectManager;

    public AgentClient(){
        this.connectManager = new AgentClientConnecManager();
    }

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter, Endpoint endpoint) throws Exception {
        Channel channel = connectManager.getChannel(endpoint);
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setInterfaceName(interfaceName);
        agentRequest.setMethod(method);
        agentRequest.setParameterTypesString(parameterTypesString);
        agentRequest.setParameter(parameter);
        AgentFuture future = new AgentFuture();

        AgentRequestHolder.put(agentRequest.getId(), future);
        channel.writeAndFlush(agentRequest);
        Object result = null;
        try{
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
