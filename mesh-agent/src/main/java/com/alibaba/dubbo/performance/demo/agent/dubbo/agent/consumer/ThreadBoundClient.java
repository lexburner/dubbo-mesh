package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.WeightRoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.registry.EndpointHolder;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.rpc.Request;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 徐靖峰
 * Date 2018-05-31
 */
public class ThreadBoundClient implements Client{

    private Map<Endpoint,Channel> channelMap = new HashMap<>(3);
    private LoadBalance loadBalance;
    private volatile boolean available = false;
    private Bootstrap b = new Bootstrap();
    private EventLoop sharedEventLoop;

    public ThreadBoundClient(EventLoop sharedEventLoop) {
        this.sharedEventLoop = sharedEventLoop;
    }

    @Override
    public Channel getChannel(){
        if(available){
            return  channelMap.get(loadBalance.select());
        }
        throw new RuntimeException("client不可用");
    }

    @Override
    public void init() {
        this.loadBalance = new WeightRoundRobinLoadBalance();
        List<Endpoint> endpoints = EndpointHolder.getEndpoints();
        System.out.println(endpoints);
        this.loadBalance.onRefresh(endpoints);
        for (Endpoint endpoint : endpoints) {
            Channel channel = connect(endpoint);
            channelMap.put(endpoint, channel);
        }
        available = true;
    }

    private Channel connect(Endpoint endpoint){
        b.group(sharedEventLoop)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DubboRpcEncoder())
                                .addLast(new DubboRpcDecoder())
                                .addLast(new ConsumerAgentHandler());
                    }
                });
        ChannelFuture f = b.connect(endpoint.getHost(), endpoint.getPort());
        return f.channel();
    }


}
