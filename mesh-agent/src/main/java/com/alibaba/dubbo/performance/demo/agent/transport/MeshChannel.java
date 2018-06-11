package com.alibaba.dubbo.performance.demo.agent.transport;

import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 徐靖峰
 * Date 2018-06-05
 */
public class MeshChannel {
    private Channel channel;
    private Endpoint endpoint;
    private AtomicLong writeCnt = new AtomicLong(0);

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public AtomicLong getWriteCnt() {
        return writeCnt;
    }

    public void setWriteCnt(AtomicLong writeCnt) {
        this.writeCnt = writeCnt;
    }
}
