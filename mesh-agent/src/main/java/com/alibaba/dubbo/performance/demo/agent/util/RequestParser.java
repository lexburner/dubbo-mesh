package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;

import java.nio.charset.StandardCharsets;
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

    public static Map<String, String> fastParse(FullHttpRequest httpRequest) {
        String content = httpRequest.content().toString(StandardCharsets.UTF_8);
        QueryStringDecoder qs = new QueryStringDecoder(content, StandardCharsets.UTF_8, false);
        Map<String, List<String>> parameters = qs.parameters();
        String interfaceName = parameters.get("interface").get(0);
        String method = parameters.get("method").get(0);
        String parameterTypesString = parameters.get("parameterTypesString").get(0);
        String parameter = parameters.get("parameter").get(0);
        Map<String, String> params = new HashMap<>();
        params.put("interface", interfaceName);
        params.put("method", method);
        params.put("parameterTypesString", parameterTypesString);
        params.put("parameter", parameter);
        return params;
    }

    public static String cheatParse(FullHttpRequest req) {
        req.retain();
        // 跳过parameter之前的部分
        req.content().skipBytes(136);
        String parameter = req.content().readCharSequence(req.content().readableBytes(), StandardCharsets.UTF_8).toString();
        req.content().release();
        return parameter;
    }

}