package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        String requestId = response.getRequestId();
        RpcCallbackFuture rpcCallbackFuture = RpcCallbackRequestHolder.get(requestId);
        if(null != rpcCallbackFuture){
            RpcRequestHolder.remove(requestId);
            rpcCallbackFuture.done(response);
        }
    }
}
