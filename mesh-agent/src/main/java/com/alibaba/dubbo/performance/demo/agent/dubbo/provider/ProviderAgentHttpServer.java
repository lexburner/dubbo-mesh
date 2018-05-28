/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.dubbo.performance.demo.agent.dubbo.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.ProviderAgentClient;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public final class ProviderAgentHttpServer {

    private Logger logger = LoggerFactory.getLogger(ProviderAgentHttpServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ServerBootstrap bootstrap;

    static final int PORT = Integer.parseInt(System.getProperty("server.port"));

    /**
     * 启动服务器接收来自 consumer-agent 的 http 请求
     */
    public void startServer() {
        try {
            new EtcdRegistry(System.getProperty("etcd.url"));
            Endpoint endpoint = new Endpoint("127.0.0.1",Integer.valueOf(System.getProperty("dubbo.protocol.port")));
            ProviderAgentClient providerAgentClient = new ProviderAgentClient(endpoint);

            bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderAgentHttpServerInitializer(providerAgentClient))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            Channel ch = bootstrap.bind(PORT+50).sync().channel();
            logger.info("provider-agent is ready to receive request from consumer-agent\n" +
                    "export at http://127.0.0.1:{}", PORT+50);
            ch.closeFuture().sync();
        } catch (Exception e) {
            logger.error("provider-agent启动失败", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("provider-agent was closed");
        }
    }

}
