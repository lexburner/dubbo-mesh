package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

@Deprecated
public class RpcClient {
    private Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ConnecManager connectManager;

    public RpcClient() {
        this.connectManager = new ConnecManager();
    }

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Channel channel = connectManager.getChannel();

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

        RpcFuture future = new RpcFuture();
        RpcRequestHolder.put(String.valueOf(providerAgentRpcRequest.getId()), future);

        channel.writeAndFlush(providerAgentRpcRequest);

        Object result = null;
        try {
            result = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
