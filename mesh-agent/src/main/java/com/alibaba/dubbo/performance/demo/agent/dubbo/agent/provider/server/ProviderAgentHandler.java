package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client.DubboClient;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import com.alibaba.dubbo.performance.demo.agent.transport.MeshChannel;
import com.alibaba.dubbo.performance.demo.agent.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Promise;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-05-17
 *
 */
public class ProviderAgentHandler extends SimpleChannelInboundHandler<Object> {

    public static FastThreadLocal<LongObjectHashMap<Promise<byte[]>>> promiseHolder = new FastThreadLocal<LongObjectHashMap<Promise<byte[]>>>() {
        @Override
        protected LongObjectHashMap<Promise<byte[]>> initialValue() throws Exception {
            return new LongObjectHashMap<>();
        }
    };
    private static FastThreadLocal<Client> dubboClientHolder = new FastThreadLocal<>();

    public ProviderAgentHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (dubboClientHolder.get() == null) {
            DubboClient dubboClient = new DubboClient(ctx.channel().eventLoop());
            dubboClient.init();
            dubboClientHolder.set(dubboClient);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof List){
            List<byte[]> requests = (List<byte[]>) msg;
            ctx.channel().eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    for (byte[] request : requests) {
                        process(ctx,request);
                    }
                }
            });
        }else {
            process(ctx,(byte[]) msg);
        }

    }

    private void process(ChannelHandlerContext ctx,byte[] msgBytes){
        long requestId = Bytes.bytes2long(msgBytes, 0);
        String param = new String(msgBytes,8,msgBytes.length-8);
        Promise<byte[]> promise = new DefaultPromise<>(ctx.executor());
        promise.addListener(future -> {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeInt(8+4);
            buffer.writeBytes((byte[]) future.get());
            ctx.channel().writeAndFlush(buffer);
        });
        promiseHolder.get().put(requestId, promise);
        MeshChannel channel = dubboClientHolder.get().getMeshChannel();
        channel.getChannel().write(messageToMessage(ctx,requestId, param));
        channel.getChannel().flush();
    }

    // header length.
    protected static final int HEADER_LENGTH = 16;
    // magic header.
    protected static final short MAGIC = (short) 0xdabb;

    protected CompositeByteBuf messageToMessage(ChannelHandlerContext ctx, long requestId, String param) {
        ByteBuf bodyBuf = ctx.alloc().ioBuffer();

        ByteBuf buffer = Unpooled.buffer();
        buffer.writeCharSequence("\"2.6.1\"\n", StandardCharsets.UTF_8);
        buffer.writeCharSequence("\"com.alibaba.dubbo.performance.demo.provider.IHelloService\"\n", StandardCharsets
                .UTF_8);
        buffer.writeCharSequence("null\n", StandardCharsets.UTF_8);
        buffer.writeCharSequence("\"hash\"\n", StandardCharsets.UTF_8);
        buffer.writeCharSequence("\"Ljava/lang/String;\"\n", StandardCharsets.UTF_8);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        buffer.release();

        String randomStr = param.substring(param.lastIndexOf("=")+1);

        bodyBuf.writeBytes(bytes);
        bodyBuf.writeCharSequence("\""+randomStr+"\"\n", StandardCharsets.UTF_8);
        bodyBuf.writeCharSequence("null\n", StandardCharsets.UTF_8);

        ByteBuf headerBuf = ctx.alloc().ioBuffer(HEADER_LENGTH);
        headerBuf.writeShort(MAGIC);
        headerBuf.writeByte(-58);
        headerBuf.writeByte(20);
        headerBuf.writeLong(requestId);
        headerBuf.writeInt(bodyBuf.readableBytes());

        CompositeByteBuf dubboRequest = PooledByteBufAllocator.DEFAULT.compositeBuffer();
        dubboRequest
                .addComponents(true,
                        headerBuf,
                        bodyBuf);
        return dubboRequest;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
