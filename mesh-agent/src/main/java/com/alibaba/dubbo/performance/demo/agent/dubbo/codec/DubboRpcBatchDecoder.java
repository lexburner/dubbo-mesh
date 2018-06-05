package com.alibaba.dubbo.performance.demo.agent.dubbo.codec;

import com.alibaba.dubbo.performance.demo.agent.dubbo.common.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.DubboRpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author 徐靖峰
 * Date 2018-06-01
 *
 * This class mainly hack the {@link io.netty.handler.codec.ByteToMessageDecoder} to provide batch submission capability.
 * This can be used the same way as ByteToMessageDecoder except the case your following inbound handler may get a decoded msg,
 * which actually is an array list, then you can submit the list of msgs to an executor to process. For example
 * <pre>
 *   if (msg instanceof List) {
 *       processorManager.getDefaultExecutor().execute(new Runnable() {
 *           @Override
 *           public void run() {
 *               // batch submit to an executor
 *               for (Object m : (List<?>) msg) {
 *                   RpcCommandHandler.this.process(ctx, m);
 *               }
 *           }
 *       });
 *   } else {
 *       process(ctx, msg);
 *   }
 * </pre>
 * You can check the method {@link AbstractBatchDecoder#channelRead(ChannelHandlerContext, Object)} ()}
 *   to know the detail modification.
 */
public class DubboRpcBatchDecoder extends AbstractBatchDecoder{
    // header length.
    protected static final int HEADER_LENGTH = 16;

    protected static final byte FLAG_EVENT = (byte) 0x20;

    private static final Logger logger = LoggerFactory.getLogger(DubboRpcBatchDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int readable = byteBuf.readableBytes();

        // 确保拿到一个完整的header
        if (readable < HEADER_LENGTH) {
            return ;
        }
        byteBuf.markReaderIndex();
        byteBuf.skipBytes(4);
        long requestId = byteBuf.readLong();
        int len = byteBuf.readInt();
        if (byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }
        DubboRpcResponse response = new DubboRpcResponse();
        response.setRequestId(requestId);
        response.setBytes(byteBuf.retainedSlice(byteBuf.readerIndex() + 2, len - 3));
        byteBuf.skipBytes(len);
        list.add(response);
    }


}
