package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentInitializer extends ChannelInitializer<SocketChannel> {

    private final String remoteHost;
    private final int remotePort;

    public ProviderAgentInitializer(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ProviderAgentHandler(remoteHost, remotePort));
    }
}
