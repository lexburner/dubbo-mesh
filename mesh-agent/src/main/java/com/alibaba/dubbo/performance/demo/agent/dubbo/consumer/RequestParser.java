package com.alibaba.dubbo.performance.demo.agent.dubbo.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-22
 */
public class RequestParser {

    /**
     * 解析请求参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     * @throws IOException
     */
//    public static Map<String, String> parse(FullHttpRequest req) throws IOException {
//        HttpMethod method = req.method();
//
//        Map<String, String> parmMap = new HashMap<>();
//
//        if (HttpMethod.GET.equals(method) ) {
//            // 是GET请求
//            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
//            decoder.parameters().forEach((key, value) -> parmMap.put(key, value.get(0)));
//        } else if (HttpMethod.POST.equals(method)) {
//            // 是POST请求
//            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
//            decoder.offer(req);
//
//            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
//
//            for (InterfaceHttpData parm : parmList) {
//
//                Attribute data = (Attribute) parm;
//                parmMap.put(data.getName(), data.getValue());
//            }
//        }
//
//        return parmMap;
//    }

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
        return params;
    }

}