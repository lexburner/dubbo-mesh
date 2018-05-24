package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseFutureHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ProviderAgentRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentClientHandler extends SimpleChannelInboundHandler<ProviderAgentRpcResponse> {

//    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProviderAgentRpcResponse response) {
        Long requestId = Long.parseLong(response.getRequestId());
        RpcCallbackFuture<ProviderAgentRpcResponse> rpcResponseRpcCallbackFuture = ConsumerAgentResponseFutureHolder.get(requestId);
        if (rpcResponseRpcCallbackFuture != null) {
            rpcResponseRpcCallbackFuture.done(response);
            ConsumerAgentResponseFutureHolder.remove(requestId);
        }
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                Long requestId = Long.parseLong(response.getRequestId());
//                DeferredResult<ResponseEntity> deferredResult = ConsumerAgentResponseFutureHolder.get(requestId);
//                if (deferredResult != null) {
//                    deferredResult.setResult(new ResponseEntity<>(new String(response.getBytes()), HttpStatus.OK));
//                    ConsumerAgentResponseFutureHolder.remove(requestId);
//                }
//            }
//        });

//        Long requestId = Long.parseLong(response.getRequestId());
//        RpcFuture rpcFuture = ConsumerAgentResponseFutureHolder.get(requestId);
//        if (rpcFuture != null) {
//            rpcFuture.done(response);
//            ConsumerAgentResponseFutureHolder.remove(requestId);
//        }
    }
}
