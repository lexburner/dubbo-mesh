package com.alibaba.dubbo.performance.demo.agent.dubbo.common;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public final class RequestParser {

    private RequestParser() {
    }

    /**
     * 解析请求参数
     */
    public static Map<String, String> parse(FullHttpRequest req) {
        Map<String, String> params = new HashMap<>();
        // 是POST请求
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
        List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
        for (InterfaceHttpData data : postList) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }
        // resolve memory leak
        decoder.destroy();
        return params;
    }

    public static Map<String, String> parseContent(FullHttpRequest req) {
        Map<String, String> params = new HashMap<>();
        req.content().retain();
        ByteBuf content = req.content();
        byte[] contentBytes = new byte[content.readableBytes()];
        content.getBytes(0,contentBytes);
        String paramsStr = URLDecoder.decode(new String(contentBytes));
        try{
            String[] paramPair = paramsStr.split("&");
            for (String param : paramPair) {
                params.put(param.substring(0,param.indexOf('=')),param.substring(param.indexOf('=') + 1));
            }
        }finally {
            req.content().release();
        }
        return params;
    }

}