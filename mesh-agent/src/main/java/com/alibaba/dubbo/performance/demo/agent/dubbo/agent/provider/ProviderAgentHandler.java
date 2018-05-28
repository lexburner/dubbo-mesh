package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.DubboResponseHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DubboRpcResponse response) {
        Long requestId = Long.parseLong(response.getRequestId());
        RpcCallbackFuture<DubboRpcResponse> rpcResponseRpcCallbackFuture = DubboResponseHolder.get(requestId);
        if (rpcResponseRpcCallbackFuture != null) {
            rpcResponseRpcCallbackFuture.done(response);
            DubboResponseHolder.remove(requestId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("ConsumerAgentHandler异常", cause);
        ctx.channel().close();
    }
}
