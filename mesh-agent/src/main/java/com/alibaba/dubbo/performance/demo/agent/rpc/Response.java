package com.alibaba.dubbo.performance.demo.agent.rpc;

import java.util.Map;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public interface Response {

    long getRequestId();

    Map<String, String> getAttachments();

    void setAttachment(String key, String value);

}
