package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.dubbo.consumer.ConsumerAgentHttpServer;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentConnectionManager {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentConnectionManager.class);
    private EventLoopGroup eventLoopGroup = ConsumerAgentHttpServer.workerGroup;

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

    ConsumerAgentConnectionManager(Endpoint endpoint) {
        this.initBootstrap();
        this.endpoint = endpoint;
        try {
            for (int i = 0; i < connectionSize; i++) {
                channelPool.add(bootstrap.connect(endpoint.getHost(), endpoint.getPort()).sync().channel());
                logger.info("agent-consumer与agent-provider{}:{}新建立了连接", endpoint.getHost(), endpoint.getPort());
            }
        } catch (Exception e) {
            logger.error("agent-consumer fail to connect agent-provider{}:{}", endpoint.getHost(), endpoint.getPort(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取连接
     */
    public Channel getChannel() {
        Channel channel = channelPool.get(Math.abs(channelCursor.addAndGet(1)) % connectionSize);
        return channel;
    }

    /**
     * 初始化连接
     */
    private void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerAgentInitializer());
    }
}
