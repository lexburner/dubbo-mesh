package com.alibaba.dubbo.performance.demo.agent.protocol.dubbo;

import com.alibaba.dubbo.performance.demo.agent.codec.AbstractBatchDecoder;
import com.alibaba.dubbo.performance.demo.agent.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-06-09
 */
public class DubboRpcBatchDecoder extends AbstractBatchDecoder {
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
        byte[] intBytes = new byte[4];
        if (status != 20) {
            Bytes.int2bytes(1, intBytes, 0);
        } else {
            byte[] bytes = new byte[len - 3];
            byteBuf.getBytes(byteBuf.readerIndex() + 2, bytes);
            int hash = Integer.parseInt(new String(bytes));
            Bytes.int2bytes(hash, intBytes, 0);
        }
        byteBuf.skipBytes(len);
        byte[] requestIdBytes = new byte[8];
        Bytes.long2bytes(requestId,requestIdBytes, 0);
        list.add(com.google.common.primitives.Bytes.concat(requestIdBytes,intBytes));
    }


}
