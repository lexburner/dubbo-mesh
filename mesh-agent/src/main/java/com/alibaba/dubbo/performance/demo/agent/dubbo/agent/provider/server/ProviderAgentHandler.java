package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client.DubboClient;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.alibaba.dubbo.performance.demo.agent.rpc.RpcCallbackFuture;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import com.alibaba.dubbo.performance.demo.agent.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentHandler extends SimpleChannelInboundHandler<DubboMeshProto.AgentRequest> {

    private static AtomicInteger cnt = new AtomicInteger(0);

    private Logger logger = LoggerFactory.getLogger(ProviderAgentHandler.class);

//    public static ThreadLocal<Map<Long,Channel>> inboundChannelMap = ThreadLocal.withInitial(HashMap::new);
    public static FastThreadLocal<LongObjectHashMap<Channel>> inboundChannelMap = new FastThreadLocal<LongObjectHashMap<Channel>>() {
        @Override
        protected LongObjectHashMap<Channel> initialValue() throws Exception {
            return new LongObjectHashMap<>();
        }
    };
//    private static ThreadLocal<Client> dubboClientHolder = new ThreadLocal<>();
    private static FastThreadLocal<Client> dubboClientHolder = new FastThreadLocal<>();

    public ProviderAgentHandler(){
        logger.info("consumer-agent => provider-agent 连接数 {}", cnt.incrementAndGet());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if(dubboClientHolder.get()==null){
            DubboClient dubboClient = new DubboClient(ctx.channel().eventLoop());
            dubboClient.init();
            dubboClientHolder.set(dubboClient);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboMeshProto.AgentRequest msg) throws Exception {
        inboundChannelMap.get().put(msg.getRequestId(), ctx.channel());
        dubboClientHolder.get().getMeshChannel().getChannel().writeAndFlush(messageToMessage(msg));
    }

    private DubboRpcRequest messageToMessage(DubboMeshProto.AgentRequest agentRequest){
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
            JsonUtils.writeObject(agentRequest.getParameter(), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        invocation.setArguments(out.toByteArray());
        DubboRpcRequest dubboRpcRequest = new DubboRpcRequest();
        dubboRpcRequest.setId(agentRequest.getRequestId());
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
