package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.consumer.ConsumerAgentHttpServerHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcResponseHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHandler.class);

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboRpcResponse dubboRpcResponse) {
        RpcCallbackFuture rpcCallbackFuture = RpcResponseHolder.get(dubboRpcResponse.getRequestId());
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(dubboRpcResponse.getBytes()));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, KEEP_ALIVE);
        rpcCallbackFuture.getChannel().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("consumerAgentHandler出现异常", cause);
        ctx.channel().close();
    }

}
