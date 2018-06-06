package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.DubboMeshProto;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.ProviderAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcClientHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboRpcResponse msg) throws Exception {
        Channel inboundChannel = ProviderAgentHandler.inboundChannelMap.get().get(msg.getRequestId());
        if(inboundChannel!=null){
            inboundChannel.writeAndFlush(messageToMessage(msg)).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    msg.getBytes().release();
                }
            });
            ProviderAgentHandler.inboundChannelMap.get().remove(msg.getRequestId());
        }
    }

    private DubboMeshProto.AgentResponse messageToMessage(DubboRpcResponse dubboRpcResponse){
        ByteBuf bytes = dubboRpcResponse.getBytes();
        byte[] result = new byte[bytes.readableBytes()];
        bytes.readBytes(result);
//        logger.info("接收到请求{}",agentRequest.toString());
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
