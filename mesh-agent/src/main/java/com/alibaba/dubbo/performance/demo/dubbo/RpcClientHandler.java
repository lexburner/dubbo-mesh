package com.alibaba.dubbo.performance.demo.dubbo;

import com.alibaba.dubbo.performance.demo.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcRequestHolder;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        RpcFuture future = RpcRequestHolder.get(requestId);
        if(null != future){
            RpcRequestHolder.remove(requestId);
            future.done(response);
        }
    }
}
