package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

        // TCP粘包
        if (byteBuf.readableBytes() < HEADER_LENGTH){
            return;
        }

        // 获取bytebuf的字节流，不改变reader index
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(),bytes);
        // 获取数据长度
        int len = Bytes.bytes2int(Arrays.copyOfRange(bytes, HEADER_LENGTH - 4, HEADER_LENGTH));
        if (byteBuf.readableBytes() < 16 + len){
            return;
        }

        byte[] data = new byte[16 + len];
        byteBuf.getBytes(byteBuf.readerIndex(), data, 0, HEADER_LENGTH + len);

        // dubbo返回的body中，前后各有一个换行，去掉
        byte[] subArray = Arrays.copyOfRange(data,HEADER_LENGTH + 2, data.length -1 );

        String s = new String(subArray);

        byte[] requestIdBytes = Arrays.copyOfRange(data,4,12);
        long requestId = Bytes.bytes2long(requestIdBytes,0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(subArray);

        list.add(response);

        byteBuf.readerIndex(HEADER_LENGTH + len);
        byteBuf.discardReadBytes();
    }
}
