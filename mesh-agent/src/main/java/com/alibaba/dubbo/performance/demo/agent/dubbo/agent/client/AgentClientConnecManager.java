package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class AgentClientConnecManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private Bootstrap bootstrap;

    private Channel channel;
    private Object lock = new Object();

    public AgentClientConnecManager() {
    }

    public Channel getChannel() throws Exception {
        if (null != channel) {
            return channel;
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (lock){
                if (null == channel){
                    channel = bootstrap.connect("127.0.0.1", 9111).sync().channel();
                }
            }
        }

        return channel;
    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new AgentClientInitializer());
    }
}
