package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.ThreadBoundClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcBatchDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.provider.RpcClientHandler;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import com.alibaba.dubbo.performance.demo.agent.transport.ThreadBoundClientHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutor;
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

    static final String REMOTE_HOST = "127.0.0.1";
    static final int REMOTE_PORT = Integer.valueOf(System.getProperty("dubbo.protocol.port"));

    public static Channel outboundChannel;

    /**
     * 启动服务器
     */
    public void startServer() {
        new EtcdRegistry(System.getProperty("etcd.url"));
        initThreadBoundClient(workerGroup);
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

    private void initThreadBoundClient(EventLoopGroup eventLoopGroup){
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup.next())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DubboRpcEncoder())
                                .addLast(new DubboRpcDecoder())
                                .addLast(new RpcClientHandler());
                    }
                });
        ChannelFuture f = b.connect(REMOTE_HOST, REMOTE_PORT);
        ProviderAgentServer.outboundChannel = f.channel();
    }



}
