/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.dubbo.performance.demo.agent.dubbo.consumer;

import com.alibaba.dubbo.performance.demo.agent.cluster.Cluster;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.RequestParser;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.rpc.DefaultRequest;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    private final Cluster<DubboRpcResponse> cluster;

    ConsumerAgentHttpServerHandler(Cluster<DubboRpcResponse> cluster) {
        this.cluster = cluster;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        processRequest(ctx, req);
    }

    private void processRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        Map<String, String> requestParams;
        requestParams = RequestParser.parse(req);

//        if (req.refCnt() > 1) {
//            req.release();
//        }

        DefaultRequest defaultRequest = new DefaultRequest();
        defaultRequest.setInterfaceName(requestParams.get("interface"));
        defaultRequest.setMethod(requestParams.get("method"));
        defaultRequest.setParameterTypesString(requestParams.get("parameterTypesString"));
        defaultRequest.setParameter(requestParams.get("parameter"));

//        int i = defaultRequest.getParameter().hashCode();
//        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer((i + "")
//                .getBytes()));
//        response.headers().set(CONTENT_TYPE, "text/plain");
//        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
//        ctx.executor().schedule(() -> {
//            if (!keepAlive) {
//                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//
//
//            } else {
//                response.headers().set(CONNECTION, KEEP_ALIVE);
//                ctx.writeAndFlush(response);
//            }
//        }, 50, TimeUnit.MILLISECONDS);
        RpcCallbackFuture<DubboRpcResponse> rpcCallbackFuture = cluster.asyncCall(defaultRequest);
        rpcCallbackFuture.addListener(rpcFuture -> {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(rpcFuture
                    .getResponse().getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

//                if(ctx.channel().isWritable())
//                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            if (!keepAlive) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.writeAndFlush(response).addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                    }
                });
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
