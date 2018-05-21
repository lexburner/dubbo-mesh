package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcAsyncClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.FutureListener;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentServerHandler extends SimpleChannelInboundHandler<AgentRequest> {

    private final RpcAsyncClient rpcClient;

    public AgentServerHandler(RpcAsyncClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final AgentRequest agentRequest) throws Exception {
        RpcCallbackFuture rpcCallbackFuture = rpcClient.invoke(agentRequest.getInterfaceName(), agentRequest.getMethod(), agentRequest.getParameterTypesString(), agentRequest.getParameter());
        rpcCallbackFuture.addListener(new FutureListener() {
            @Override
            public void operationComplete(RpcCallbackFuture future) {
                RpcResponse response = future.getResponse();
                AgentResponse agentResponse = new AgentResponse();
                agentResponse.setValue(new String(response.getBytes(), Charset.forName("utf-8")));
                agentResponse.setId(agentRequest.getId());
                ctx.writeAndFlush(agentResponse);
            }
        });

//        Future<Object> future = worker.submit(() -> {
//            Object result = null;
//            result =  rpcClient.invoke(agentRequest.getInterfaceName(), agentRequest.getMethod(), agentRequest.getParameterTypesString(), agentRequest.getParameter());
//            return result;
//        });
//        future.addListener(new GenericFutureListener<Future<? super Object>>() {
//            @Override
//            public void operationComplete(Future<? super Object> future) throws Exception {
//                AgentResponse agentResponse = new AgentResponse();
//                agentResponse.setValue(new String((byte[]) future.get(), Charset.forName("utf-8")));
//                agentResponse.setId(agentRequest.getId());
//                ctx.writeAndFlush(agentResponse);
//            }
//        });
    }

}
