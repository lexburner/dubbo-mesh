package com.alibaba.dubbo.performance.demo.agent.dubbo.consumer;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

import java.util.concurrent.Callable;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class ConsumerClient {

    public ConsumerClient() {
        System.out.println("==>ConsumerClient构造...");
    }

    EventLoopGroup eventExecutors = new DefaultEventLoopGroup(10);

    public Future<Integer> invoke(final String parameter) {
        Future<Integer> future = eventExecutors.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return parameter.hashCode();
            }
        });
        return future;
    }

}
