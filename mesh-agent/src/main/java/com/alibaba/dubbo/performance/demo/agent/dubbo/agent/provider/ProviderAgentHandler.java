package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.provider.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentHandler.class);

    private final String remoteHost;
    private final int remotePort;

    // As we use inboundChannel.eventLoop() when building the Bootstrap this does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as the inboundChannel.
    private Channel outboundChannel;

    public ProviderAgentHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
//        logger.info("agent-consumer与agent-provider新建立了连接...{}",a.incrementAndGet());
        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new RpcClientHandler(inboundChannel))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(remoteHost, remotePort);
        outboundChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        // was able to flush out data, start to read the next chunk
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
