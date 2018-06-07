package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client;

import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server.ProviderAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcResponse;
import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.List;

public class DubboRpcHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

    public DubboRpcHandler() {
        System.out.println("DubboRpcHandler...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboRpcResponse msg) throws Exception {
            process(msg);
    }

    private void process(DubboRpcResponse msg) {
        Channel inboundChannel = ProviderAgentHandler.inboundChannelMap.get().get(msg.getRequestId());
        if (inboundChannel != null) {
            inboundChannel.writeAndFlush(messageToMessage(msg)).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    msg.getBytes().release();
                }
            });
            ProviderAgentHandler.inboundChannelMap.get().remove(msg.getRequestId());
        }
    }

    private DubboMeshProto.AgentResponse messageToMessage(DubboRpcResponse dubboRpcResponse) {
        ByteBuf bytes = dubboRpcResponse.getBytes();
        byte[] result = new byte[bytes.readableBytes()];
        bytes.readBytes(result);
        return DubboMeshProto.AgentResponse.newBuilder()
                .setRequestId(dubboRpcResponse.getRequestId())
                .setHash(ByteString.copyFrom(result))
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
