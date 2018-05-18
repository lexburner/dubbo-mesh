package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class AgentServerConnecManager {
    EventLoopGroup bossGroup = new NioEventLoopGroup(4);
    EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    private ServerBootstrap bootstrap;

    public void initBootstrap() {
        try{

        bootstrap = new ServerBootstrap()
                .group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new AgentServerInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind("127.0.0.1", 9111);
            future.syncUninterruptibly();
        }catch (Exception e){
            e.printStackTrace();
        }
//        finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
//        }
    }
}
