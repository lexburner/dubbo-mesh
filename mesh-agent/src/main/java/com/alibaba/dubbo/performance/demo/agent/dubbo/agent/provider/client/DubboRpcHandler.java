package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server.ProviderAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DubboRpcHandler extends SimpleChannelInboundHandler<Object> {

    static final Logger logger = LoggerFactory.getLogger(DubboRpcHandler.class);

    public DubboRpcHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof List){
            List<DubboRpcResponse> responses = (List<DubboRpcResponse>) msg;
            for (DubboRpcResponse response : responses) {
                process(response);
            }
        }else if(msg instanceof DubboRpcResponse){
            process((DubboRpcResponse) msg);
        }

    }

    private void process(DubboRpcResponse msg) {
        Promise<Integer> promise = ProviderAgentHandler.promiseHolder.get().remove(msg.getRequestId());
        if (promise != null) {
            promise.trySuccess(messageToMessage(msg));
        }
    }

    private int messageToMessage(DubboRpcResponse dubboRpcResponse) {
        return Integer.parseInt(new String(dubboRpcResponse.getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
