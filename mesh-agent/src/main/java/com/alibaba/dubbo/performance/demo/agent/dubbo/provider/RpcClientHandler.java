package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private final Channel.Unsafe unsafe;

    public RpcClientHandler(Channel inboundChannel) {
        unsafe = inboundChannel.unsafe();
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
        unsafe.write(msg, ctx.voidPromise());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        unsafe.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("RpcClientHandler转发异常", cause);
        ctx.channel().close();
    }

}
