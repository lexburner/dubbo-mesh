package com.alibaba.dubbo.performance.demo.agent.protocol.dubbo;


public class DubboRpcResponse {

    private long requestId;
    private byte[] bytes;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
