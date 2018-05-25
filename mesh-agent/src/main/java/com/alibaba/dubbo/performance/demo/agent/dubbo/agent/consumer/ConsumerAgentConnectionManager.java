package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ConsumerAgentConnectionManager {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentConnectionManager.class);
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Map<Endpoint, Channel> channelPool = new HashMap<>();

    private Bootstrap bootstrap;

    private final Object lock = new Object();

    /**
     * 获取连接
     * @param endpoint
     */
    public Channel getChannel(Endpoint endpoint) {
        Channel channel = channelPool.get(endpoint);
        if (null != channel) {
            return channel;
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        synchronized (lock) {
            channel = channelPool.get(endpoint);
            if(channel!= null) {
                return channel;
            }
            try {
                channel = bootstrap.connect(endpoint.getHost(), endpoint.getPort()).sync().channel();
                logger.info("agent-consumer与agent-provider{}:{}新建立了连接", endpoint.getHost(), endpoint.getPort());
                channelPool.put(endpoint, channel);
            } catch (Exception e) {
                logger.error("agent-consumer fail to connect agent-provider{}:{}", endpoint.getHost(), endpoint.getPort(), e);
                throw new RuntimeException(e);
            }
        }

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
