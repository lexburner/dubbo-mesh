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

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.FutureListener;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentHttpServerHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    final ConsumerClient consumerClient;
    final ExecutorService executorService;

    ConsumerAgentHttpServerHandler(ConsumerClient consumerClient,ExecutorService executorService) {
        this.consumerClient = consumerClient;
        this.executorService = executorService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (msg instanceof FullHttpRequest) {
                    FullHttpRequest req = (FullHttpRequest) msg;

                    if (req.uri().equals("/favicon.ico")) {
                        return;
                    }

                    Map<String, String> requestParams = null;
                    try {
                        requestParams = RequestParser.parse(req);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    boolean keepAlive = HttpUtil.isKeepAlive(req);

                    String interfaceName = requestParams.get("interface");
                    String method = requestParams.get("method");
                    String parameterTypesString = requestParams.get("parameterTypesString");
                    String parameter = requestParams.get("parameter");

                    RpcCallbackFuture<AgentResponse> rpcCallbackFuture = consumerClient.invoke(interfaceName, method, parameterTypesString, parameter);
                    rpcCallbackFuture.addListener(new FutureListener<AgentResponse>() {
                        @Override
                        public void operationComplete(RpcCallbackFuture<AgentResponse> rpcFuture) {
                            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(rpcFuture.getResponse().getValue().getBytes()));
                            response.headers().set(CONTENT_TYPE, "text/plain");
                            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

                            if (!keepAlive) {
                                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                            } else {
                                response.headers().set(CONNECTION, KEEP_ALIVE);
                                ctx.writeAndFlush(response);
                            }
                        }
                    });
                }
            }
        });
    }

    //            requestParams = new HashMap<>();
//            requestParams.put("interface", "com.alibaba.dubbo.performance.demo.provider.IHelloService");
//            requestParams.put("method", "hash");
//            requestParams.put("parameterTypesString", "Ljava/lang/String;");
//            requestParams.put("parameter", "123");

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        if(ctx.channel().isActive()) ctx.close();
    }
}
