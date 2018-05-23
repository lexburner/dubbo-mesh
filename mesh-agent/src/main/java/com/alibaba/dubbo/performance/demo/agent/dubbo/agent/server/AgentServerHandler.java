package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcAsyncClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.FutureListener;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentServerHandler extends SimpleChannelInboundHandler<AgentRequest> {

    private final RpcAsyncClient rpcClient;

    Logger logger = LoggerFactory.getLogger(AgentServerHandler.class);

    public AgentServerHandler(RpcAsyncClient rpcClient) {
        this.rpcClient = rpcClient;
        logger.info("AgentServerHandler构造...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final AgentRequest agentRequest)  {
        try{
            RpcCallbackFuture<ProviderAgentRpcResponse> rpcCallbackFuture = rpcClient.invoke(agentRequest.getInterfaceName(), agentRequest.getMethod(), agentRequest.getParameterTypesString(), agentRequest.getParameter());
            rpcCallbackFuture.addListener(new FutureListener<ProviderAgentRpcResponse>() {
                @Override
                public void operationComplete(RpcCallbackFuture<ProviderAgentRpcResponse> future) {
                    ProviderAgentRpcResponse providerAgentRpcResponse = future.getResponse();
                    AgentResponse agentResponse = new AgentResponse();
                    agentResponse.setId(agentRequest.getId());
                    agentResponse.setValue(new String(providerAgentRpcResponse.getBytes()));
                    ctx.writeAndFlush(agentResponse);
                }
            });
        }catch (Exception e){
            logger.error("AgentServerHandler中rpcClient.invoke失败",e);
        }
    }

}
