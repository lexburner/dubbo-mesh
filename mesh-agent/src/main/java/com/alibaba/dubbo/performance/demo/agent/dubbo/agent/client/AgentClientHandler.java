package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentCallbackRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public class AgentClientHandler extends SimpleChannelInboundHandler<AgentResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        long requestId = response.getId();
        DeferredResult<ResponseEntity> deferredResult = AgentCallbackRequestHolder.get(requestId);
        deferredResult.setResult(new ResponseEntity<>(Integer.valueOf(response.getValue()), HttpStatus.OK));
        AgentRequestHolder.remove(requestId);
    }
}
