package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentServer {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ServerBootstrap bootstrap;

    /**
     * 启动服务器
     */
    public void startServer() {
        new EtcdRegistry(System.getProperty("etcd.url"));
        try {
            bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderAgentInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            int port = Integer.valueOf(System.getProperty("server.port"));
            Channel channel = bootstrap.bind(IpHelper.getHostIp(), port + 50).sync().channel();
            logger.info("provider-agent provider is ready to receive request from consumer-agent\n" +
                    "export at 127.0.0.1:{}", port + 50);
            channel.closeFuture().sync();
        } catch (Exception e) {
            logger.error("provider-agent启动失败", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("provider-agent provider was closed");
        }
    }

}
