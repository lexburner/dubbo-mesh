package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.Callable;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentServerHandler extends SimpleChannelInboundHandler<AgentRequest> {

    private final RpcClient rpcClient;

    public AgentServerHandler(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    EventExecutorGroup worker = new DefaultEventExecutorGroup(100);

    Logger logger = LoggerFactory.getLogger(AgentServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final AgentRequest agentRequest) throws Exception {
        Future<Object> future = worker.submit(() -> {
            Object result = null;
            result =  rpcClient.invoke(agentRequest.getInterfaceName(), agentRequest.getMethod(), agentRequest.getParameterTypesString(), agentRequest.getParameter());
            return result;
        });
        future.addListener(new GenericFutureListener<Future<? super Object>>() {
            @Override
            public void operationComplete(Future<? super Object> future) throws Exception {
                AgentResponse agentResponse = new AgentResponse();
                agentResponse.setValue(new String((byte[]) future.get(), Charset.forName("utf-8")));
                agentResponse.setId(agentRequest.getId());
                ctx.writeAndFlush(agentResponse);
            }
        });
    }

}
