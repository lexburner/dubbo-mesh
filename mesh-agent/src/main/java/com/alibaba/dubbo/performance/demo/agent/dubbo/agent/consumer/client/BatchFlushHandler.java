package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.consumer.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class BatchFlushHandler extends ChannelOutboundHandlerAdapter {
    private long nBatch = 0;
    private static final int MAX_BATCH_COUNT = 5;

    private CompositeByteBuf compositeByteBuf;
    private boolean preferComposite;

    public BatchFlushHandler() {
        this(true);
    }

    public BatchFlushHandler(boolean preferComposite) {
        this.preferComposite = preferComposite;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (preferComposite) {
            compositeByteBuf = ctx.alloc().compositeBuffer(MAX_BATCH_COUNT);
        }
        super.handlerAdded(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (preferComposite) {
            compositeByteBuf.addComponent(true, (ByteBuf) msg);
        } else {
            ctx.write(msg);
        }

    }

    @Override
    public void flush(ChannelHandlerContext ctx) {
        ++nBatch;
        if (nBatch % MAX_BATCH_COUNT == 0) {

            if (preferComposite) {
                ctx.writeAndFlush(compositeByteBuf).addListener(future -> compositeByteBuf = ctx.alloc().compositeBuffer
                        (MAX_BATCH_COUNT));
            } else {
                ctx.flush();
            }
        }
    }
}
