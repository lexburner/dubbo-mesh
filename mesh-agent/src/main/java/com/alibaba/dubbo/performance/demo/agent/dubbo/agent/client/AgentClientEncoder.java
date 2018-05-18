package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentSerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class AgentClientEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (in instanceof AgentRequest) {
            byte[] serialize = AgentSerializationUtil.serializeRequest((AgentRequest) in);
            out.writeInt(serialize.length);
            out.writeBytes(serialize);
        }
    }


}
