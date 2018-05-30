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

import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.ConsumerAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.RequestParser;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.rpc.DefaultRequest;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.rpc.Request;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    private Channel outboundChannel;

    private LoadBalance loadBalance;

    ConsumerAgentHttpServerHandler(LoadBalance loadBalance){
        this.loadBalance = loadBalance;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .option(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DubboRpcEncoder())
                                .addLast(new DubboRpcDecoder())
                                .addLast(new ConsumerAgentHandler(inboundChannel));
                    }
                }).option(ChannelOption.AUTO_READ, false);
        Endpoint endpoint = loadBalance.select();
        ChannelFuture f = b.connect(endpoint.getHost(), endpoint.getPort());
        outboundChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        processRequest(ctx,req);
    }

    private void processRequest(ChannelHandlerContext ctx,FullHttpRequest req) {
        Map<String, String> requestParams;
        requestParams = RequestParser.parse(req);

        DefaultRequest defaultRequest = new DefaultRequest();
        defaultRequest.setInterfaceName(requestParams.get("interface"));
        defaultRequest.setMethod(requestParams.get("method"));
        defaultRequest.setParameterTypesString(requestParams.get("parameterTypesString"));
        defaultRequest.setParameter(requestParams.get("parameter"));

        this.call(ctx,outboundChannel,defaultRequest);

    }

    public void call(ChannelHandlerContext ctx,Channel channel,Request request) {
        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(request.getMethod());
        invocation.setAttachment("path", request.getInterfaceName());
        invocation.setParameterTypes(request.getParameterTypesString());    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        try {
            JsonUtils.writeObject(request.getParameter(), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        invocation.setArguments(out.toByteArray());
        DubboRpcRequest dubboRpcRequest = new DubboRpcRequest();
        dubboRpcRequest.setVersion("2.0.0");
        dubboRpcRequest.setTwoWay(true);
        dubboRpcRequest.setData(invocation);
//        logger.info("requestId=" + dubboRpcRequest.getId());
        channel.writeAndFlush(dubboRpcRequest).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // was able to flush out data, start to read the next chunk
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        closeOnFlush(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
