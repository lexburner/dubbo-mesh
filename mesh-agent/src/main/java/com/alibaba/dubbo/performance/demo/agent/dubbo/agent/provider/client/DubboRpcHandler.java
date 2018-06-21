package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server.ProviderAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.util.Bytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-06-07
 */
public class DubboRpcHandler extends SimpleChannelInboundHandler<Object> {

    static final Logger logger = LoggerFactory.getLogger(DubboRpcHandler.class);

    public DubboRpcHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof List){
            List<byte[]> responses = (List<byte[]>) msg;
            for (byte[] response : responses) {
                process(response);
            }
        }else {
            process((byte[]) msg);
        }
    }

    private void process(byte[] msg) {
        long requestId = Bytes.bytes2long(msg, 0);
        Promise<byte[]> promise = ProviderAgentHandler.promiseHolder.get().remove(requestId);
        if (promise != null) {
            promise.trySuccess(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
