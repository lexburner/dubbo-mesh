package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client.BatchFlushHandler;
import com.alibaba.dubbo.performance.demo.agent.protocol.simple.SimpleDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline
                .addLast(new SimpleDecoder())
                .addLast(new BatchFlushHandler(false))
                .addLast(new ProviderAgentHandler());
    }
}
