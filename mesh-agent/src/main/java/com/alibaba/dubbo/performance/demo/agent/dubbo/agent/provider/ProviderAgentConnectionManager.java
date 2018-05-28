package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentConnectionManager {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentConnectionManager.class);
    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private volatile boolean available;

    private List<Channel> channelPool = new ArrayList<>();
    AtomicInteger channelCursor = new AtomicInteger(0);
    private Bootstrap bootstrap;

    private static final int connectionSize = 1;

    private Endpoint endpoint;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    ProviderAgentConnectionManager(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 获取连接
     */
    public Channel getChannel() {
        if(available){
            Channel channel = channelPool.get(Math.abs(channelCursor.addAndGet(1)) % connectionSize);
            return channel;
        }
        throw new RuntimeException("channel not init,call init first");
    }

    public void init(){
        initBootstrap();
        initChannelPool();
        available = true;
    }

    private void initChannelPool() {
        try {
            for (int i = 0; i < connectionSize; i++) {
                channelPool.add(bootstrap.connect(endpoint.getHost(), endpoint.getPort()).sync().channel());
                logger.info("provider-agent与provider{}:{}新建立了连接", endpoint.getHost(), endpoint.getPort());
            }
        } catch (Exception e) {
            logger.error("provider-agent fail to connect to agent与provider{}:{}", endpoint.getHost(), endpoint.getPort(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化连接
     */
    private void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ProviderAgentInitializer());
    }
}
