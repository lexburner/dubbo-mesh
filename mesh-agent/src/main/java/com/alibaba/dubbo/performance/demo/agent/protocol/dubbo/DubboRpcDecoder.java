package com.alibaba.dubbo.performance.demo.agent.protocol.dubbo;

import com.alibaba.dubbo.performance.demo.agent.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[HEADER_LENGTH];
        byteBuf.readBytes(header);
//        byte[] dataLen = Arrays.copyOfRange(header, 12, 16);
//        int len = Bytes.bytes2int(dataLen);
        int len = Bytes.bytes2int(header,12);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.readerIndex(savedReaderIndex);
        byte[] data = new byte[tt];
        byteBuf.readBytes(data);


        //byte[] data = new byte[byteBuf.readableBytes()];
        //byteBuf.readBytes(data);

        // HEADER_LENGTH + 1，忽略header & Response value type的读取，直接读取实际Return value
        // dubbo返回的body中，前后各有一个换行，去掉
        byte[] subArray = Arrays.copyOfRange(data, HEADER_LENGTH + 2, data.length - 1);

//        String s = new String(subArray);

//        byte[] requestIdBytes = Arrays.copyOfRange(data, 4, 12);
//        long requestId = Bytes.bytes2long(requestIdBytes, 0);
        long requestId = Bytes.bytes2long(data, 4);
//        logger.info("consumer-agent发送dubbo请求编号:{}", requestId);
        DubboRpcResponse response = new DubboRpcResponse();
        response.setRequestId(requestId);
        response.setBytes(subArray);
        return response;
    }
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
//
//        int readable = byteBuf.readableBytes();
//
//        // 确保拿到一个完整的header
//        if (readable < HEADER_LENGTH) {
//            return ;
//        }
//        byteBuf.markReaderIndex();
//        byteBuf.skipBytes(3);
//        byte status = byteBuf.readByte();
//        long requestId = byteBuf.readLong();
//        int len = byteBuf.readInt();
//        if (byteBuf.readableBytes() < len) {
//            byteBuf.resetReaderIndex();
//            return;
//        }
//        DubboRpcResponse response = new DubboRpcResponse();
//        if(status != 20){
//            response.setBytes(Unpooled.wrappedBuffer(new byte[]{1}));
//        }else {
//            response.setBytes(byteBuf.retainedSlice(byteBuf.readerIndex() + 2, len - 3));
//        }
//        byteBuf.skipBytes(len);
//        response.setRequestId(requestId);
//        list.add(response);
//    }

}
