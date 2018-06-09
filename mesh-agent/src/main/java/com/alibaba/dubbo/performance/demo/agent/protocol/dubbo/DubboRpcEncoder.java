package com.alibaba.dubbo.performance.demo.agent.protocol.dubbo;

import com.alibaba.dubbo.performance.demo.agent.util.JsonUtils;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DubboRpcEncoder extends MessageToByteEncoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;
    // magic header.
    protected static final short MAGIC = (short) 0xdabb;
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;

//    static {
//        ByteBuf buffer = Unpooled.directBuffer();
//        buffer.writeCharSequence("\"2.6.1\"\n", StandardCharsets.UTF_8);
//        buffer.writeCharSequence("\"com.alibaba.dubbo.performance.demo.provider.IHelloService\"\n", StandardCharsets
//                .UTF_8);
//        buffer.writeCharSequence("null\n", StandardCharsets.UTF_8);
//        buffer.writeCharSequence("\"hash\"\n", StandardCharsets.UTF_8);
//        buffer.writeCharSequence("\"Ljava/lang/String;\"\n", StandardCharsets.UTF_8);
//        fixedComponent = buffer;
//    }
//
//    private static final ByteBuf fixedComponent;

    /**
     * 优化点：zero-copy
     *
     * @param ctx
     * @param msg
     * @param buffer
     * @throws Exception
     */
//    @Override
//    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
//        DubboRpcRequest req = (DubboRpcRequest) msg;
//        RpcInvocation rpcInvocation = (RpcInvocation)req.getData();
//        ByteBuf bodyBuf = Unpooled.directBuffer();
//        bodyBuf.writeBytes(fixedComponent.duplicate());
//        buffer.writeCharSequence(JSON.toJSONString(new String(rpcInvocation.getArguments()))+"\n", StandardCharsets.UTF_8);
//        buffer.writeCharSequence("null\n", StandardCharsets.UTF_8);
//
//        ByteBuf headerBuf = ctx.alloc().ioBuffer(HEADER_LENGTH);
//        headerBuf.writeShort(MAGIC);
//        headerBuf.writeByte(getFlag(req));
//        headerBuf.writeByte(20);
//        headerBuf.writeLong(req.getId());
//        headerBuf.writeInt(bodyBuf.readableBytes());
//
//        ((CompositeByteBuf) buffer).addComponent(headerBuf);
//        ((CompositeByteBuf) buffer).addComponent(bodyBuf);
//        ((CompositeByteBuf) buffer).writerIndex(headerBuf.readableBytes() + bodyBuf.readableBytes());
//    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
        DubboRpcRequest req = (DubboRpcRequest) msg;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encodeRequestData(bos, req.getData());
        ByteBuf bodyBuf = Unpooled.wrappedBuffer(bos.toByteArray());

        ByteBuf headerBuf = ctx.alloc().ioBuffer(HEADER_LENGTH);
        headerBuf.writeShort(MAGIC);
        headerBuf.writeByte(getFlag(req));
        headerBuf.writeByte(20);
        headerBuf.writeLong(req.getId());
        headerBuf.writeInt(bodyBuf.readableBytes());

        ((CompositeByteBuf) buffer).addComponent(headerBuf);
        ((CompositeByteBuf) buffer).addComponent(bodyBuf);
        ((CompositeByteBuf) buffer).writerIndex(headerBuf.readableBytes() + bodyBuf.readableBytes());
    }

    private byte getFlag(DubboRpcRequest req) {
        byte flag = FLAG_REQUEST | 6;
        if (req.isTwoWay()) flag |= FLAG_TWOWAY;
        if (req.isEvent()) flag |= FLAG_EVENT;

        return flag;
    }


    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, Object msg, boolean preferDirect) {
        return ctx.alloc().compositeDirectBuffer(2);
    }

    public void encodeRequestData(OutputStream out, Object data) throws Exception {
        RpcInvocation inv = (RpcInvocation) data;

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

        JsonUtils.writeObject(inv.getAttachment("dubbo", "2.0.1"), writer);
        JsonUtils.writeObject(inv.getAttachment("path"), writer);
        JsonUtils.writeObject(inv.getAttachment("version"), writer);
        JsonUtils.writeObject(inv.getMethodName(), writer);
        JsonUtils.writeObject(inv.getParameterTypes(), writer);

        JsonUtils.writeBytes(inv.getArguments(), writer);
        JsonUtils.writeObject(inv.getAttachments(), writer);
    }


}
