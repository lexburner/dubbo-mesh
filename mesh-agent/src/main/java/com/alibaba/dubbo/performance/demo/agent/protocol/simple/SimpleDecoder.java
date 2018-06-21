package com.alibaba.dubbo.performance.demo.agent.protocol.simple;

import com.alibaba.dubbo.performance.demo.agent.codec.AbstractBatchDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-06-14
 */
public class SimpleDecoder extends AbstractBatchDecoder {

    private final static int HEADER_LEN = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEADER_LEN) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(data);
    }
}
