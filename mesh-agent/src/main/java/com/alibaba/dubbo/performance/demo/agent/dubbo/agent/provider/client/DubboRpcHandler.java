package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server.ProviderAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.google.protobuf.ByteString;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DubboRpcHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

    static final Logger logger = LoggerFactory.getLogger(DubboRpcHandler.class);

    private static AtomicInteger cnt = new AtomicInteger(0);
    public DubboRpcHandler() {
        logger.info("provider-agent => provider 连接数 {}", cnt.incrementAndGet());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboRpcResponse msg) throws Exception {
            process(msg);
    }

    private void process(DubboRpcResponse msg) {
        Channel inboundChannel = ProviderAgentHandler.inboundChannelMap.get().get(msg.getRequestId());
        if (inboundChannel != null) {
            inboundChannel.writeAndFlush(messageToMessage(msg));
            ProviderAgentHandler.inboundChannelMap.get().remove(msg.getRequestId());
        }
    }

    private DubboMeshProto.AgentResponse messageToMessage(DubboRpcResponse dubboRpcResponse) {
        return DubboMeshProto.AgentResponse.newBuilder()
                .setRequestId(dubboRpcResponse.getRequestId())
                .setHash(ByteString.copyFrom(dubboRpcResponse.getBytes()))
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
