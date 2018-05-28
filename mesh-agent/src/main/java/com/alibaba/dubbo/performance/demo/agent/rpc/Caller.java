package com.alibaba.dubbo.performance.demo.agent.rpc;

import io.netty.channel.Channel;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public interface Caller {

    void call(Channel channel,Request request);

}
