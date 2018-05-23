package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.CommonThreadPool;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<ProviderAgentRpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProviderAgentRpcResponse providerAgentRpcResponse) {
        CommonThreadPool.executorService.execute(new Runnable() {
            @Override
            public void run() {
                String requestId = providerAgentRpcResponse.getRequestId();
                RpcCallbackFuture<ProviderAgentRpcResponse> rpcCallbackFuture = ProviderAgentRpcResponseFutureHolder.get(requestId);
                if (null != rpcCallbackFuture) {
                    ProviderAgentRpcResponseFutureHolder.remove(requestId);
                    rpcCallbackFuture.done(providerAgentRpcResponse);
                }
            }
        });
    }
}
