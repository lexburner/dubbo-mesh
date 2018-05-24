package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public class AgentClientHandler extends SimpleChannelInboundHandler<ProviderAgentRpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProviderAgentRpcResponse response) {
        Long requestId = Long.parseLong(response.getRequestId());
        DeferredResult<ResponseEntity> deferredResult = ConsumerAgentResponseFutureHolder.get(requestId);
        if (deferredResult != null) {
            deferredResult.setResult(new ResponseEntity<>(new String(response.getBytes()), HttpStatus.OK));
            ConsumerAgentResponseFutureHolder.remove(requestId);
        }
    }
}
