package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.provider.RpcAsyncClient;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentServerInitializer extends ChannelInitializer<SocketChannel> {

    //TODO 测试连接复用
//    private RpcAsyncClient rpcAsyncClient = new RpcAsyncClient();

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new AgentServerDecoder());
        pipeline.addLast(new AgentServerEncoder());
        pipeline.addLast(new AgentServerHandler(new RpcAsyncClient()));
    }
}
