package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentServerConnecManager {

    Logger logger = LoggerFactory.getLogger(AgentServerConnecManager.class);

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(1);

    private ServerBootstrap bootstrap;

    public void initBootstrap() {
        try{

        bootstrap = new ServerBootstrap()
                .group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new AgentServerInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = bootstrap.bind(IpHelper.getHostIp(), port+50);
            logger.info("agent server export port at {}:{}",IpHelper.getHostIp(), port+50);
//            future.syncUninterruptibly();
        }catch (Exception e){
            e.printStackTrace();
        }
//        finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
//        }
    }
}
