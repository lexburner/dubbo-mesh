package com.alibaba.dubbo.performance.demo.agent.dubbo.codec;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
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

        try {
            do {
                int savedReaderIndex = byteBuf.readerIndex();
                Object msg = null;
                try {
                    msg = decode2(byteBuf);
                } catch (Exception e) {
                    throw e;
                }
                if (msg == DecodeResult.NEED_MORE_INPUT) {
                    byteBuf.readerIndex(savedReaderIndex);
                    break;
                }

                list.add(msg);
            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }


        //list.add(decode2(byteBuf));
    }

    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_INPUT
    }

    /**
     * Demo为简单起见，直接从特定字节位开始读取了的返回值，demo未做：
     * 1. 请求头判断
     * 2. 返回值类型判断
     *
     * @param byteBuf
     * @return
     */
    private Object decode2(ByteBuf byteBuf) {
        int readable = byteBuf.readableBytes();

        // 确保拿到一个完整的header
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.skipBytes(4);
        long requestId = byteBuf.readLong();
        int len = byteBuf.readInt();

        if (byteBuf.readableBytes() < len) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        DubboRpcResponse response = new DubboRpcResponse();
        response.setRequestId(requestId);
        response.setBytes(byteBuf.retainedSlice(byteBuf.readerIndex(), len - 1));
        byteBuf.skipBytes(1); // 尾部换行

        return response;
    }
}
