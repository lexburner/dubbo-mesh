package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AgentClientHandler extends SimpleChannelInboundHandler<AgentResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        long requestId = response.getId();
        RpcCallbackFuture<AgentResponse> responseRpcCallbackFuture = ConsumerAgentResponseFutureHolder.get(requestId);
        if(responseRpcCallbackFuture!=null){
            responseRpcCallbackFuture.done(response);
            ConsumerAgentResponseFutureHolder.remove(requestId);
        }

    }
}
