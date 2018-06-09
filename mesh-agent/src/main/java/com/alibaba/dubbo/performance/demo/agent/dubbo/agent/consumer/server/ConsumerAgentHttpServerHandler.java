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
package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client.ConsumerAgentClient;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.rpc.ThreadBoundRpcResponseHolder;
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import com.alibaba.dubbo.performance.demo.agent.util.RequestParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public ConsumerAgentHttpServerHandler() {
    }

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    public static AtomicLong requestIdGenerator = new AtomicLong(0);

    private static AtomicInteger handlerCnt = new AtomicInteger(0);

//    private Endpoint channelConsistenceHashEndpoint;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        int handlerNo = handlerCnt.incrementAndGet();
//        this.channelConsistenceHashEndpoint = ConsumerAgentHttpServer.remoteEndpoints[handlerNo % ConsumerAgentHttpServer.remoteEndpoints.length];
//        logger.info("bound channel now is {}", handlerNo);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        processRequest(ctx, req);
    }

    private void processRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        Map<String, String> requestParams = RequestParser.fastParse(req);

        DubboMeshProto.AgentRequest agentRequest = DubboMeshProto.AgentRequest.newBuilder().setRequestId(requestIdGenerator.incrementAndGet())
//                .setInterfaceName(requestParams.get("interface"))
//                .setMethod(requestParams.get("method"))
//                .setParameterTypesString(requestParams.get("parameterTypesString"))
                .setParameter(requestParams.get("parameter"))
                .build();

        this.call(ctx, agentRequest);
    }

    public void call(ChannelHandlerContext ctx, DubboMeshProto.AgentRequest request) {
        MeshChannel meshChannel = ConsumerAgentClient.get(ctx.channel().eventLoop().toString()).getMeshChannel();
        RpcCallbackFuture rpcCallbackFuture = new RpcCallbackFuture<>();
        rpcCallbackFuture.setChannel(ctx.channel());
        rpcCallbackFuture.setEndpoint(meshChannel.getEndpoint());
        ThreadBoundRpcResponseHolder.put(request.getRequestId(), rpcCallbackFuture);
        meshChannel.getChannel().writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        ctx.channel().close();
    }


}
