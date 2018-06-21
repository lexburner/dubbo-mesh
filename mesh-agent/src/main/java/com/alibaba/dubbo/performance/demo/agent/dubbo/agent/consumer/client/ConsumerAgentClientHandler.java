package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.server.ConsumerAgentHttpServerFastHandler;
import com.alibaba.dubbo.performance.demo.agent.util.Bytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentClientHandler extends SimpleChannelInboundHandler<Object> {

    public ConsumerAgentClientHandler() {
    }

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof List){
            List<byte[]> responses = (List<byte[]>) msg;
            for (byte[] response : responses) {
                callback(response);
            }
        }else{
            callback((byte[])msg);
        }

    }

    private void callback(byte[] agentResponse) {
        long requestId = Bytes.bytes2long(agentResponse,0);
        int hash = Bytes.bytes2int(agentResponse,8);
        Promise<Integer> promise = ConsumerAgentHttpServerFastHandler.promiseHolder.get().remove(requestId);
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
