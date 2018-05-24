package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnecManager {

    Logger logger = LoggerFactory.getLogger(ConnecManager.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Bootstrap bootstrap;

    private Channel channel;
    private Object lock = new Object();

    public ConnecManager() {
        System.out.println("ConnecManager构造...");
    }

    public Channel getChannel() {
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
            synchronized (lock) {
                if (null == channel) {
                    try {
                        int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
                        channel = bootstrap.connect("127.0.0.1", port).sync().channel();
                        logger.info("provider-agent 与 provider({}:{})新建立了连接,连接地址", "127.0.0.1", port);
                    } catch (Exception e) {
                        logger.error("连接失败", e);
                    }
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
                .handler(new RpcClientInitializer());
    }
}
