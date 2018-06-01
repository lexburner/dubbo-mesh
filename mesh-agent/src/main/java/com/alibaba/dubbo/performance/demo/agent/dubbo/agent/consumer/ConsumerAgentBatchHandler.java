package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcResponseHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentBatchHandler extends SimpleChannelInboundHandler<Object> {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentBatchHandler.class);

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof DubboRpcResponse){
            DubboRpcResponse dubboRpcResponse = (DubboRpcResponse) msg;
            RpcCallbackFuture rpcCallbackFuture = RpcResponseHolder.get(dubboRpcResponse.getRequestId());
            if(rpcCallbackFuture!=null){
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(dubboRpcResponse.getBytes()));
                response.headers().set(CONTENT_TYPE, "text/plain");
                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(CONNECTION, KEEP_ALIVE);
                rpcCallbackFuture.getChannel().writeAndFlush(response);
                RpcResponseHolder.remove(dubboRpcResponse.getRequestId());
            }
        }else if(msg instanceof List){
            List dubboRpcResponses = (List) msg;
            for (Object item : dubboRpcResponses) {
                DubboRpcResponse dubboRpcResponse = (DubboRpcResponse) item;
                RpcCallbackFuture rpcCallbackFuture = RpcResponseHolder.get(dubboRpcResponse.getRequestId());
                if(rpcCallbackFuture!=null){
                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(dubboRpcResponse.getBytes()));
                    response.headers().set(CONTENT_TYPE, "text/plain");
                    response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    rpcCallbackFuture.getChannel().writeAndFlush(response);
                    RpcResponseHolder.remove(dubboRpcResponse.getRequestId());
                }
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("consumerAgentHandler出现异常", cause);
        ctx.channel().close();
    }

}
