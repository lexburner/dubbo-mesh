package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client.DubboClient;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import com.alibaba.dubbo.performance.demo.agent.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentHandler.class);

    public static FastThreadLocal<LongObjectHashMap<Promise<Integer>>> promiseHolder = new FastThreadLocal<LongObjectHashMap<Promise<Integer>>>() {
        @Override
        protected LongObjectHashMap<Promise<Integer>> initialValue() throws Exception {
            return new LongObjectHashMap<>();
        }
    };
    private static FastThreadLocal<Client> dubboClientHolder = new FastThreadLocal<>();

    public ProviderAgentHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (dubboClientHolder.get() == null) {
            DubboClient dubboClient = new DubboClient(ctx.channel().eventLoop());
            dubboClient.init();
            dubboClientHolder.set(dubboClient);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        long requestId = msg.readLong();
        String param = msg.toString(StandardCharsets.UTF_8);
        Promise<Integer> promise = new DefaultPromise<>(ctx.executor());
        promise.addListener(future -> {
            ByteBuf buffer = Unpooled.buffer();
            int hash = (Integer) future.get();
            buffer.writeLong(requestId);
            buffer.writeInt(hash);
            ctx.channel().writeAndFlush(buffer);

        });
        promiseHolder.get().put(requestId, promise);
        MeshChannel channel = dubboClientHolder.get().getMeshChannel();
        channel.getChannel().write(messageToMessage(requestId, param));
//        if (channel.getWriteCnt().incrementAndGet() % 2 == 0) {
            channel.getChannel().flush();
//        }
    }

    private DubboRpcRequest messageToMessage(Long requestId, String param) {
        RpcInvocation invocation = new RpcInvocation();
//        invocation.setMethodName(agentRequest.getMethod());
//        invocation.setAttachment("path", agentRequest.getInterfaceName());
//        invocation.setParameterTypes(agentRequest.getParameterTypesString());
        invocation.setMethodName("hash");
        invocation.setAttachment("path", "com.alibaba.dubbo.performance.demo.provider.IHelloService");
        invocation.setParameterTypes("Ljava/lang/String;");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        try {
            JsonUtils.writeObject(param, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        invocation.setArguments(out.toByteArray());
        DubboRpcRequest dubboRpcRequest = new DubboRpcRequest();
        dubboRpcRequest.setId(requestId);
        dubboRpcRequest.setVersion("2.0.0");
        dubboRpcRequest.setTwoWay(true);
        dubboRpcRequest.setData(invocation);
        return dubboRpcRequest;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
