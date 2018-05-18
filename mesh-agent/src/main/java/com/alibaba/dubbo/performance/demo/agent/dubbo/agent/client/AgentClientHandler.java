package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AgentClientHandler extends SimpleChannelInboundHandler<AgentResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        long requestId = response.getId();
        AgentFuture future = AgentRequestHolder.get(requestId);
        if(null != future){
            AgentRequestHolder.remove(requestId);
            future.done(response);
        }
    }
}
