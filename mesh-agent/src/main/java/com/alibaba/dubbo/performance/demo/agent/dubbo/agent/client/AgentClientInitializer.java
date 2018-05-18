package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new AgentClientEncoder());
        pipeline.addLast(new AgentClientDecoder());
        pipeline.addLast(new AgentClientHandler());
    }
}
