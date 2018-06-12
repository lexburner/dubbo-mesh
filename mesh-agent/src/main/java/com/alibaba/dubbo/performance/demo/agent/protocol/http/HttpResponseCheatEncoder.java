package com.alibaba.dubbo.performance.demo.agent.protocol.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 徐靖峰
 * Date 2018-06-12
 */
public class HttpResponseCheatEncoder extends MessageToByteEncoder<String>{

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        out.writeBytes(Unpooled.wrappedBuffer(getResponse(msg).getBytes()));
    }

    private String getResponse(String hash){
        String response = "HTTP/1.1 200 OK\r\n"+
                "Connection: keep-alive\r\n"+
                "Content-Type: text/plain;charset=UTF-8\r\n"+
                "Content-Length: "+hash.length()+"\r\n\r\n"+
                hash;
        return response;
    }
}
