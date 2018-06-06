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

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.DubboMeshProto;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.RequestParser;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.rpc.*;
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import com.alibaba.dubbo.performance.demo.agent.transport.ThreadBoundClientHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    public static AtomicLong requestIdGenerator = new AtomicLong(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        processRequest(ctx,req);
    }

    private void processRequest(ChannelHandlerContext ctx,FullHttpRequest req) {
        Map<String, String> requestParams;
        requestParams = RequestParser.parse(req);

        DubboMeshProto.AgentRequest agentRequest = DubboMeshProto.AgentRequest.newBuilder().setRequestId(requestIdGenerator.incrementAndGet())
                .setInterfaceName(requestParams.get("interface"))
                .setMethod(requestParams.get("method"))
                .setParameterTypesString(requestParams.get("parameterTypesString"))
                .setParameter(requestParams.get("parameter"))
                .build();

        this.call(ctx,agentRequest);
    }

    public void call(ChannelHandlerContext ctx,DubboMeshProto.AgentRequest request) {
        MeshChannel meshChannel = ThreadBoundClientHolder.get(ctx.channel().eventLoop().toString()).getChannel();
        Endpoint endpoint = meshChannel.getEndpoint();
        RpcCallbackFuture rpcCallbackFuture = new RpcCallbackFuture<>();
        rpcCallbackFuture.setChannel(ctx.channel());
        rpcCallbackFuture.setEndpoint(endpoint);
        ThreadBoundRpcResponseHolder.put(request.getRequestId(), rpcCallbackFuture);
        meshChannel.getChannel().writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        ctx.channel().close();
    }



}
