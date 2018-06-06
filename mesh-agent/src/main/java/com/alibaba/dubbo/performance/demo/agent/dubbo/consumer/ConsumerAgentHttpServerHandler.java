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

import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.RequestParser;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.rpc.*;
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import com.alibaba.dubbo.performance.demo.agent.transport.RateLimiter;
import com.alibaba.dubbo.performance.demo.agent.transport.ThreadBoundClientHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public ConsumerAgentHttpServerHandler(){
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("avtive=>"+ctx.channel().eventLoop());
//        if(clientHolder.get()==null){
//            Client client = new ThreadBoundClient(ctx.channel().eventLoop());
//            client.init();
//            clientHolder.set(client);
//        }
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

        this.call(ctx,defaultRequest);

    }

    public void call(ChannelHandlerContext ctx,Request request) {
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

//        logger.info("请求发送成功:{}",dubboRpcRequest.getId());
        MeshChannel meshChannel = ThreadBoundClientHolder.get(ctx.channel().eventLoop().toString()).getChannel();
        Endpoint endpoint = meshChannel.getEndpoint();
//        AtomicInteger requestCnt = RateLimiter.endpointAtomicIntegerMap.get(endpoint);
//        if(requestCnt.incrementAndGet()>200){
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE);
//            response.headers().set(CONTENT_TYPE, "text/plain");
//            response.headers().set(CONNECTION, KEEP_ALIVE);
//            ctx.writeAndFlush(response);
//            requestCnt.decrementAndGet();
//        }

        RpcCallbackFuture<DubboRpcResponse> rpcCallbackFuture = new RpcCallbackFuture<>();
        rpcCallbackFuture.setChannel(ctx.channel());
        rpcCallbackFuture.setEndpoint(endpoint);
        ThreadBoundRpcResponseHolder.put(dubboRpcRequest.getId(), rpcCallbackFuture);
        meshChannel.getChannel().writeAndFlush(dubboRpcRequest);
    }

    static AtomicInteger errors = new AtomicInteger(0);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        ctx.channel().close();
    }



}
