package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new DubboRpcEncoder());
        pipeline.addLast(new DubboRpcDecoder());
        pipeline.addLast(new ConsumerAgentHandler());
    }
}
