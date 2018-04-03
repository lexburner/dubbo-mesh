package com.alibaba.dubbo.performance.demo.dubbo;

import com.alibaba.dubbo.performance.demo.dubbo.model.Request;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcResponse;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
//        pipeline.addLast("logging", new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new DubboRpcEncoder(Request.class));
        pipeline.addLast(new DubboRpcDecoder(RpcResponse.class));
        pipeline.addLast(new RpcClientHandler());
    }
}
