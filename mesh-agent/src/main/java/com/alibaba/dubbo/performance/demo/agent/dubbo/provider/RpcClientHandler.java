package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private final Channel inboundChannel;

    public RpcClientHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
//        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
//        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                if (future.isSuccess()) {
//                    ctx.channel().read();
//                } else {
//                    future.channel().close();
//                }
//            }
//        });
        inboundChannel.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("RpcClientHandler转发异常", cause);
        ctx.channel().close();
    }

}
