package com.alibaba.dubbo.performance.demo.agent.protocol.dubbo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;

    protected static final byte FLAG_EVENT = (byte) 0x20;

    private static final Logger logger = LoggerFactory.getLogger(DubboRpcDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

        int readable = byteBuf.readableBytes();

        // 确保拿到一个完整的header
        if (readable < HEADER_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        byteBuf.skipBytes(3);
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();
        int len = byteBuf.readInt();
        if (byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }
        DubboRpcResponse response = new DubboRpcResponse();
        if (status != 20) {
            response.setBytes("1".getBytes());
//            response.setBytes(new byte[]{1});
        } else {
            byte[] bytes = new byte[len - 3];
            byteBuf.getBytes(byteBuf.readerIndex() + 2, bytes);
            response.setBytes(bytes);
        }
        byteBuf.skipBytes(len);
        response.setRequestId(requestId);
        list.add(response);
    }


}
