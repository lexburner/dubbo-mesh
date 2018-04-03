package com.alibaba.dubbo.performance.demo.dubbo;

import com.alibaba.dubbo.performance.demo.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.dubbo.model.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;

    // magic header.
    protected static final short MAGIC = (short) 0xdabb;
    protected static final byte MAGIC_HIGH = Bytes.short2bytes(MAGIC)[0];
    protected static final byte MAGIC_LOW = Bytes.short2bytes(MAGIC)[1];
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;
    protected static final int SERIALIZATION_MASK = 0x1f;

    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_SOME_INPUT
    }

    private Class<?> clazz;
    public DubboRpcDecoder(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
       System.out.println("Received Dubbo Response data...");
       list.add(decode2(byteBuf));

//       int savedReaderIndex = byteBuf.readerIndex();
//       try {
//           do {
//               Object msg = null;
//               try {
//                   msg = decode(byteBuf);
//               } catch (Exception e) {
//                   throw e;
//               }
//               if (msg == DecodeResult.NEED_MORE_INPUT) {
//                   byteBuf.readerIndex(savedReaderIndex);
//                   break;
//               }
//
//               list.add(msg);
//           } while (byteBuf.isReadable());
//       } finally {
//           if (byteBuf.isReadable()) {
//               byteBuf.discardReadBytes();
//           }
//       }
    }

    // 简单起见，直接从指定位置取值
    protected Object decode (ByteBuf byteBuf) throws Exception {
        int readable = byteBuf.readableBytes();
        byte[] data = new byte[readable];
        byteBuf.readBytes(data);

        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] dataLen = Arrays.copyOfRange(data,12,16);
        int len = Bytes.bytes2int(dataLen);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] requestIdBytes = Arrays.copyOfRange(data,4,12);
        long requestId = Bytes.bytes2long(requestIdBytes,0);


        byte[] dataBytes = new byte[0];
        if (len > 0) {
            dataBytes = Arrays.copyOfRange(data, 16, 16 + len);
        }

        RpcResponse response = new RpcResponse();
        response.setBytes(dataBytes);
        response.setRequestId(String.valueOf(requestId));
        return response;
    }


    private Object decode2(ByteBuf byteBuf){
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        byte[] subArray = Arrays.copyOfRange(data,17,data.length);

        String s = new String(subArray);

        byte[] requestIdBytes = Arrays.copyOfRange(data,4,12);
        long requestId = Bytes.bytes2long(requestIdBytes,0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(subArray);
        //response.setResult(s);
        return response;
    }
}
