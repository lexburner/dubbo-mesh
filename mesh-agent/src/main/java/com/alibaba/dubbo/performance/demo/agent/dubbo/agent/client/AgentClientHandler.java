package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.http.HttpStatus;

public class AgentClientHandler extends SimpleChannelInboundHandler<ProviderAgentRpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProviderAgentRpcResponse response) {
        Long requestId = Long.parseLong(response.getRequestId());
        RpcCallbackFuture<ProviderAgentRpcResponse> rpcResponseRpcCallbackFuture = ConsumerAgentResponseFutureHolder.get(requestId);
        rpcResponseRpcCallbackFuture.done(response);
        ConsumerAgentResponseFutureHolder.remove(requestId);
//        Long requestId = Long.parseLong(response.getRequestId());
//        DeferredResult<ResponseEntity> deferredResult = ConsumerAgentResponseFutureHolder.get(requestId);
//        if (deferredResult != null) {
//            deferredResult.setResult(new ResponseEntity<>(new String(response.getBytes()), HttpStatus.OK));
//            ConsumerAgentResponseFutureHolder.remove(requestId);
//        }
    }
}
