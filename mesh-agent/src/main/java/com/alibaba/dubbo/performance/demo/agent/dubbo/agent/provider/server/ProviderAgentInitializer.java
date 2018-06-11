package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server;

import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
//        pipeline.addLast("protobufVarint32FrameDecoder", new ProtobufVarint32FrameDecoder());
//        pipeline.addLast("protobufDecoder", new ProtobufDecoder(DubboMeshProto.AgentRequest.getDefaultInstance()));
//        pipeline.addLast("protobufVarint32LengthFieldPrepender", new ProtobufVarint32LengthFieldPrepender());
//        pipeline.addLast("protobufEncoder", new ProtobufEncoder());
        pipeline
                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2))
                .addLast(new LengthFieldPrepender(2))
                .addLast(new ProviderAgentHandler());
    }
}
