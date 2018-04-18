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
       list.add(decode2(byteBuf));
    }

    /**
     * Demo为简单起见，直接从特定字节位开始读取了的返回值，demo未做：
     * 1. 请求头判断
     * 2. 返回值类型判断
     *
     * @param byteBuf
     * @return
     */
    private Object decode2(ByteBuf byteBuf){
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        // HEADER_LENGTH + 1，忽略header & Response value type的读取，直接读取实际Return value
        byte[] subArray = Arrays.copyOfRange(data,HEADER_LENGTH + 1, data.length);

        String s = new String(subArray);

        byte[] requestIdBytes = Arrays.copyOfRange(data,4,12);
        long requestId = Bytes.bytes2long(requestIdBytes,0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(subArray);
        return response;
    }
}
