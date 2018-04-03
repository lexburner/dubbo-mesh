package com.alibaba.dubbo.performance.demo.dubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ConnecManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    //private Map<String,List<ChannelWrapper>> channelsByService = new LinkedHashMap<>();

    private Bootstrap bootstrap;
    private Channel channel;

    public Channel getChannel(String serviceName) throws Exception {
        if ( null == bootstrap){
            initBootstrap();
        }
        Channel channel = bootstrap.connect("127.0.0.1",20889).sync().channel();
        return channel;
    }

    public void initBootstrap() throws Exception {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());

    }
}
