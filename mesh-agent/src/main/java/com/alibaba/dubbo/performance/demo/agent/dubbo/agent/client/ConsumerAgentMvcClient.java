//package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;
//
//import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
//import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
//import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
//import io.netty.channel.Channel;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.context.request.async.DeferredResult;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// * @author 徐靖峰[OF2938]
// * company qianmi.com
// * Date 2018-05-17
// * <p>
// * consumer-agent 向 provider-agent 发起调用的客户端
// */
//public class ConsumerAgentMvcClient {
//
//    private AgentClientConnecManager connectManager;
//
//    public ConsumerAgentMvcClient() {
//        this.connectManager = new AgentClientConnecManager();
//    }
//
//
//    public DeferredResult<ResponseEntity> invoke(String interfaceName, String method, String parameterTypesString, String parameter, Endpoint endpoint) throws Exception {
//        Channel channel = connectManager.getChannel(endpoint);
//        AgentRequest agentRequest = new AgentRequest();
//        agentRequest.setInterfaceName(interfaceName);
//        agentRequest.setMethod(method);
//        agentRequest.setParameterTypesString(parameterTypesString);
//        agentRequest.setParameter(parameter);
//        channel.writeAndFlush(agentRequest);
//
//        DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>();
//        ConsumerAgentResponseFutureHolder.put(agentRequest.getId(), deferredResult);
//        return deferredResult;
//    }
//}
