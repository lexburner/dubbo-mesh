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
package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.server;

import com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance.WeightRoundRobinLoadBalance;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client.ConsumerAgentClient;
import com.alibaba.dubbo.performance.demo.agent.registry.EndpointHolder;
import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public final class ConsumerAgentHttpServer {

    private Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServer.class);

    private EventLoopGroup bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);

    private ServerBootstrap bootstrap;

    static final int PORT = Integer.parseInt(System.getProperty("server.port"));

    public static Endpoint[] remoteEndpoints;

    /**
     * 启动服务器接收来自 consumer 的 http 请求
     */
    public void startServer() {
        try {
            initThreadBoundClient(workerGroup);
            extractEndpoints();

            bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(bossGroup, workerGroup)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ConsumerAgentHttpServerInitializer())
                    .childOption(ChannelOption.TCP_NODELAY, true);
            Channel ch = bootstrap.bind(PORT).sync().channel();
            logger.info("consumer-agent provider is ready to receive request from consumer\n" +
                    "export at http://127.0.0.1:{}", PORT);
            ch.closeFuture().sync();
        } catch (Exception e) {
            logger.error("consumer-agent start failed", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("consumer-agent provider was closed");
        }
    }

    private void extractEndpoints() {
        WeightRoundRobinLoadBalance loadBalance = new WeightRoundRobinLoadBalance();
        List<Endpoint> endpoints = EndpointHolder.getEndpoints();
        loadBalance.onRefresh(endpoints);
        remoteEndpoints = loadBalance.getOriginEndpoints();
    }


    private void initThreadBoundClient(EventLoopGroup eventLoopGroup) {
        for (EventExecutor eventExecutor : eventLoopGroup) {
            if (eventExecutor instanceof EventLoop) {
                ConsumerAgentClient consumerAgentClient = new ConsumerAgentClient((EventLoop) eventExecutor);
                consumerAgentClient.init();
                ConsumerAgentClient.put(eventExecutor.toString(), consumerAgentClient);
            }

        }
    }

}
