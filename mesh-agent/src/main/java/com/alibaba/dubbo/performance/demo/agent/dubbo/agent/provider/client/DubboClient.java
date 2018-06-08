package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client;

import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author 徐靖峰
 * Date 2018-06-07
 */
public class DubboClient implements Client {

    private static final String REMOTE_HOST = "127.0.0.1";
    private static final int REMOTE_PORT = Integer.valueOf(System.getProperty("dubbo.protocol.port"));

    private static final Endpoint providerEndponit = new Endpoint(REMOTE_HOST,REMOTE_PORT);

    private EventLoopGroup eventExecutors;

    public DubboClient(){
        this.eventExecutors = new NioEventLoopGroup(1);
    }

    public DubboClient(EventLoopGroup eventExecutors) {
        this.eventExecutors = eventExecutors;
    }

    private MeshChannel meshChannel;

    @Override
    public void init() {
        Bootstrap b = new Bootstrap();
        b.group(eventExecutors.next())
                .channel(NioSocketChannel.class)
                .handler(new DubboRpcInitializer())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        ChannelFuture f = b.connect(REMOTE_HOST, REMOTE_PORT);
        MeshChannel meshChannel = new MeshChannel();
        meshChannel.setChannel(f.channel());
        meshChannel.setEndpoint(providerEndponit);
        this.meshChannel = meshChannel;
    }

    @Override
    public MeshChannel getChannel() {
        return this.meshChannel;
    }

    @Override
    public MeshChannel getChannel(Endpoint endpoint) {
        return null;
    }
}
