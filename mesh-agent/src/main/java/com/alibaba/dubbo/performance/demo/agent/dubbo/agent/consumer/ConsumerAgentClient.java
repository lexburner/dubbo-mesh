//package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;
//
//import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;
//import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcRequest;
//import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
//import com.alibaba.dubbo.performance.demo.agent.rpc.Request;
//import com.alibaba.dubbo.performance.demo.agent.transport.Client;
//import io.netty.channel.Channel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//
///**
// * @author 徐靖峰
// * Date 2018-05-22
// */
//public class ConsumerAgentClient implements Client {
//
//    private Logger logger = LoggerFactory.getLogger(ConsumerAgentClient.class);
//
//    /**
//     * consumerAgent发起请求的入口
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public void call(Channel channel,Request request) {
//        RpcInvocation invocation = new RpcInvocation();
//        invocation.setMethodName(request.getMethod());
//        invocation.setAttachment("path", request.getInterfaceName());
//        invocation.setParameterTypes(request.getParameterTypesString());    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
//        try{
//            JsonUtils.writeObject(request.getParameter(), writer);
//        }catch (IOException e){
//            throw new RuntimeException(e);
//        }
//        invocation.setArguments(out.toByteArray());
//        DubboRpcRequest dubboRpcRequest = new DubboRpcRequest();
//        dubboRpcRequest.setVersion("2.0.0");
//        dubboRpcRequest.setTwoWay(true);
//        dubboRpcRequest.setData(invocation);
////        logger.info("requestId=" + dubboRpcRequest.getId());
//        channel.writeAndFlush(dubboRpcRequest);
//    }
//}
