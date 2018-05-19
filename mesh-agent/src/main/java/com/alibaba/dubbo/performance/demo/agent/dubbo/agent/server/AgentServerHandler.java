package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    ExecutorService executorService = Executors.newFixedThreadPool(20);

    Logger logger = LoggerFactory.getLogger(AgentServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final AgentRequest agentRequest) throws Exception {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Object result = null;
                try{
                    result =  rpcClient.invoke(agentRequest.getInterfaceName(), agentRequest.getMethod(), agentRequest.getParameterTypesString(), agentRequest.getParameter());
                }catch (Exception e){
                    logger.error("provider 调用失败", e);
                    result = "OK".getBytes();
                }
                AgentResponse agentResponse = new AgentResponse();
                agentResponse.setValue(new String((byte[]) result, Charset.forName("utf-8")));
                agentResponse.setId(agentRequest.getId());
                ctx.writeAndFlush(agentResponse);
            }
        });
    }

}
