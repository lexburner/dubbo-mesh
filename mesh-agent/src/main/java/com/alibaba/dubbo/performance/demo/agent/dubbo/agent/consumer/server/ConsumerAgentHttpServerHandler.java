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
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import com.alibaba.dubbo.performance.demo.agent.util.RequestParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerAgentHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    public ConsumerAgentHttpServerHandler() {
    }

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    public static AtomicLong requestIdGenerator = new AtomicLong(0);

//    private static AtomicInteger handlerCnt = new AtomicInteger(0);

    public static FastThreadLocal<LongObjectHashMap<Promise>> promiseHolder = new FastThreadLocal<LongObjectHashMap<Promise>>() {
        @Override
        protected LongObjectHashMap<Promise> initialValue() throws Exception {
            return new LongObjectHashMap<>();
        }
    };

    private Endpoint channelConsistenceHashEndpoint;

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

        long requestId = requestIdGenerator.incrementAndGet();

        req.retain();//Increases the reference count by 1
        CompositeByteBuf agentRequest = Unpooled.compositeBuffer();
        req.content().skipBytes(136);
        agentRequest
                .addComponents(true,
                        Unpooled.copyLong(requestId),
                        req.content().slice());

        Promise<Integer> agentResponsePromise = new DefaultPromise<>(ctx.executor());
        agentResponsePromise.addListener(future -> {
            int agentResponse = (Integer) future.get();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(Integer.toString(agentResponse).getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.channel().writeAndFlush(response);
        });
        promiseHolder.get().put(requestId, agentResponsePromise);
        MeshChannel meshChannel = ConsumerAgentClient.get(ctx.channel().eventLoop().toString()).getMeshChannel();
        meshChannel.getChannel().write(agentRequest);
//        if(meshChannel.getWriteCnt().incrementAndGet()%2==0){
            meshChannel.getChannel().flush();
//        }
    }


//    private void processRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
////        Map<String, String> requestParams = RequestParser.fastParse(req);
////
////        DubboMeshProto.AgentRequest agentRequest = DubboMeshProto.AgentRequest.newBuilder().setRequestId(requestIdGenerator.incrementAndGet())
////                .setInterfaceName(requestParams.get("interface"))
////                .setMethod(requestParams.get("method"))
////                .setParameterTypesString(requestParams.get("parameterTypesString"))
////                .setParameter(requestParams.get("parameter"))
////                .build();
//
//        DubboMeshProto.AgentRequest agentRequest = DubboMeshProto.AgentRequest.newBuilder().setRequestId(requestIdGenerator.incrementAndGet())
//                .setParameter(RequestParser.cheatParse(req))
//                .build();
//
//        this.call(ctx, agentRequest);
//    }

//    public void call(ChannelHandlerContext ctx, DubboMeshProto.AgentRequest request) {
//        Promise<DubboMeshProto.AgentResponse> agentResponsePromise = new DefaultPromise<>(ctx.executor());
//        agentResponsePromise.addListener(future -> {
//            DubboMeshProto.AgentResponse agentResponse = (DubboMeshProto.AgentResponse) future.get();
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(agentResponse.getHash().toByteArray()));
//            response.headers().set(CONTENT_TYPE, "text/plain");
//            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
//            response.headers().set(CONNECTION, KEEP_ALIVE);
//            ctx.writeAndFlush(response);
//        });
//        promiseHolder.get().put(request.getRequestId(), agentResponsePromise);
//        MeshChannel meshChannel = ConsumerAgentClient.get(ctx.channel().eventLoop().toString()).getMeshChannel(channelConsistenceHashEndpoint);
//        meshChannel.getChannel().writeAndFlush(request);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        ctx.channel().close();
    }


}
