package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.server.ConsumerAgentHttpServerHandler;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public ConsumerAgentClientHandler() {
    }

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentClientHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        callback(msg);
    }

    private void callback(ByteBuf agentResponse) {
        long requestId = agentResponse.readLong();
        int hash = agentResponse.readInt();
        Promise<Integer> promise = ConsumerAgentHttpServerHandler.promiseHolder.get().remove(requestId);
        if(promise !=null){
            promise.trySuccess(hash);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("consumerAgentHandler出现异常", cause);
        ctx.channel().close();
    }

}
