package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RpcAsyncClient {
    private Logger logger = LoggerFactory.getLogger(RpcAsyncClient.class);

    private ConnecManager connectManager;
    private Channel channel;
    public RpcAsyncClient() {
        this.connectManager = new ConnecManager();
        this.channel = connectManager.getChannel();
        logger.info("构造RpcAsyncClient...");
    }

    public RpcCallbackFuture<ProviderAgentRpcResponse> invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        ProviderAgentRpcRequest providerAgentRpcRequest = new ProviderAgentRpcRequest();
        providerAgentRpcRequest.setVersion("2.0.0");
        providerAgentRpcRequest.setTwoWay(true);
        providerAgentRpcRequest.setData(invocation);

        logger.info("requestId=" + providerAgentRpcRequest.getId());

        RpcCallbackFuture<ProviderAgentRpcResponse> future = new RpcCallbackFuture<>();
        ProviderAgentRpcResponseFutureHolder.put(String.valueOf(providerAgentRpcRequest.getId()), future);

        channel.writeAndFlush(providerAgentRpcRequest);
        return future;
    }
}
