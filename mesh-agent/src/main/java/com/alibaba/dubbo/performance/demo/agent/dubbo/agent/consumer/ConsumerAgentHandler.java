package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.ConsumerAgentResponseHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.RpcCallbackFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

//    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DubboRpcResponse response) {
        Long requestId = Long.parseLong(response.getRequestId());
        RpcCallbackFuture<DubboRpcResponse> rpcResponseRpcCallbackFuture = ConsumerAgentResponseHolder.get(requestId);
        if (rpcResponseRpcCallbackFuture != null) {
            rpcResponseRpcCallbackFuture.done(response);
            ConsumerAgentResponseHolder.remove(requestId);
        }
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                Long requestId = Long.parseLong(response.getRequestId());
//                DeferredResult<ResponseEntity> deferredResult = ConsumerAgentResponseHolder.get(requestId);
//                if (deferredResult != null) {
//                    deferredResult.setResult(new ResponseEntity<>(new String(response.getBytes()), HttpStatus.OK));
//                    ConsumerAgentResponseHolder.remove(requestId);
//                }
//            }
//        });

    }
}
