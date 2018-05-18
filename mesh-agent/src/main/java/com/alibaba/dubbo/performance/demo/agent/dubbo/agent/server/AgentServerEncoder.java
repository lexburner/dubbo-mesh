package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model.AgentSerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class AgentServerEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if(in instanceof AgentResponse){
            byte[] serialize = AgentSerializationUtil.serializeResponse((AgentResponse) in);
            out.writeInt(serialize.length);
            out.writeBytes(serialize);
        }
    }


}
