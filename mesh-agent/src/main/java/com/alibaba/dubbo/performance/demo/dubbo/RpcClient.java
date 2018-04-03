package com.alibaba.dubbo.performance.demo.dubbo;

import com.alibaba.dubbo.performance.demo.dubbo.model.JsonUtils;
import com.alibaba.dubbo.performance.demo.dubbo.model.Request;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcRequestHolder;

import io.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class RpcClient {

    private ConnecManager connectManager;

    public RpcClient(){
        this.connectManager = new ConnecManager();
    }

    public Object hello(String name) throws Exception {

        Channel channel = connectManager.getChannel("");

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName("hello");
        invocation.setAttachment("path", "com.alibaba.dubbo.performance.demo123.provider.IHelloService");
        invocation.setParameterTypes("Ljava/lang/String;");// 这块用ReflecUtils转换成字符串

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(name, writer);
        invocation.setArguments(out.toByteArray());

        Request request = new Request();
        request.setVersion("2.0.0");
        request.setTwoWay(true);
        request.setData(invocation);

        System.out.println("requestId=" + request.getId());

        RpcFuture future = new RpcFuture();
        RpcRequestHolder.put(String.valueOf(request.getId()),future);

        channel.writeAndFlush(request);

        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        // result是byte數組，可能準不了
        return result;
    }
}
