package com.alibaba.dubbo.performance.demo.agent.protocol.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-06-12
 *
 * 自定义http request 解码器，仅适用于比赛
 */
public class HttpRequestFastDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String httpContent = "";
        if(in.readableBytes()<67) return;
        in.markReaderIndex();
        // "POST / HTTP/1.1\r\ncontent-length: "
        httpContent += in.readCharSequence(17+16,StandardCharsets.UTF_8);
        int contentLengthIndex = 0;
        for(int i=0;i<10;i++){
            if(in.getByte(in.readerIndex()+i)=='\r'){
                contentLengthIndex = i;
            }
        }
        String contentLength = in.readCharSequence( contentLengthIndex, StandardCharsets.UTF_8).toString();
        httpContent += contentLength;

        int contentIndex = -1 ;
        for(int i=0;i<in.readableBytes()-2;i++){
            if(in.getByte(in.readerIndex()+i)=='\r'&&in.getByte(in.readerIndex()+i+2)=='\r'){
                contentIndex = i;
            }
        }
        contentIndex = contentIndex + 3;
        String temp = in.readCharSequence( contentIndex+1, StandardCharsets.UTF_8).toString();
        httpContent += temp;

        if(in.readableBytes()<Integer.parseInt(contentLength)){
            in.resetReaderIndex();
            return;
        }

        String content = in.readCharSequence( Integer.parseInt(contentLength), StandardCharsets.UTF_8).toString();

        httpContent += content;
//        out.add(content.substring(content.lastIndexOf("=")+1));
        //将http body报文进行透传
        out.add(httpContent);
    }
}
