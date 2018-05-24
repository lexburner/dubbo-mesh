//package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;
//
//import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
//import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
//import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
//import io.netty.channel.Channel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.context.request.async.DeferredResult;
//
//import java.io.ByteArrayOutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
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
//    Logger logger = LoggerFactory.getLogger(ConsumerAgentMvcClient.class);
//
//    private AgentClientConnecManager connectManager1;
//
//    public ConsumerAgentMvcClient() {
//        this.connectManager1 = new AgentClientConnecManager();
//    }
//
//    public DeferredResult<ResponseEntity> invoke(String interfaceName, String method, String parameterTypesString, String parameter, Endpoint endpoint) throws Exception {
//        Channel channel ;
//            channel = connectManager1.getChannel(endpoint);
//
//        RpcInvocation invocation = new RpcInvocation();
//        invocation.setMethodName(method);
//        invocation.setAttachment("path", interfaceName);
//        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
//        JsonUtils.writeObject(parameter, writer);
//        invocation.setArguments(out.toByteArray());
//
//        ProviderAgentRpcRequest providerAgentRpcRequest = new ProviderAgentRpcRequest();
//        providerAgentRpcRequest.setVersion("2.0.0");
//        providerAgentRpcRequest.setTwoWay(true);
//        providerAgentRpcRequest.setData(invocation);
//
//        logger.info("requestId=" + providerAgentRpcRequest.getId());
//
//        DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>();
//        ConsumerAgentResponseFutureHolder.put(providerAgentRpcRequest.getId(), deferredResult);
//        channel.writeAndFlush(providerAgentRpcRequest);
//        return deferredResult;
//    }
//}
