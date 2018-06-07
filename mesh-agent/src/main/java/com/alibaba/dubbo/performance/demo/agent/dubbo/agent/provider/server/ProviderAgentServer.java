package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client.DubboClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client.DubboRpcInitializer;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client.DubboRpcHandler;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 */
public class ProviderAgentServer {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);

    private ServerBootstrap bootstrap;

    /**
     * 启动服务器
     */
    public void startServer() {
        new EtcdRegistry(System.getProperty("etcd.url"));

        Client client = new DubboClient(workerGroup);
        client.init();

        try {
            bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderAgentInitializer(client))
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
