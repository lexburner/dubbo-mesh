package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public class AgentClientHandler extends SimpleChannelInboundHandler<AgentResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        long requestId = response.getId();
//                RpcCallbackFuture<AgentResponse> responseRpcCallbackFuture = ConsumerAgentResponseFutureHolder.get(requestId);
//                if(responseRpcCallbackFuture!=null){
//                    responseRpcCallbackFuture.done(response);
//                    ConsumerAgentResponseFutureHolder.remove(requestId);
//                }
        DeferredResult<ResponseEntity> deferredResult = ConsumerAgentResponseFutureHolder.get(requestId);
        if (deferredResult != null) {
            deferredResult.setResult(new ResponseEntity<>(Integer.valueOf(response.getValue()), HttpStatus.OK));
            ConsumerAgentResponseFutureHolder.remove(requestId);
        }
    }
}
