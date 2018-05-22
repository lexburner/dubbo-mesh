package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import java.nio.charset.Charset;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-17
 */
public class AgentSerializationUtil {

    static Charset defaultCharset = Charset.forName("utf-8");

    static public byte[] serializeRequest(AgentRequest agentRequest) {
        String sb = String.valueOf(agentRequest.getId()) + "," +
                agentRequest.getInterfaceName() + "," +
                agentRequest.getMethod() + "," +
                agentRequest.getParameter() + "," +
                agentRequest.getParameterTypesString();
        return sb.getBytes(defaultCharset);
    }

    static public byte[] serializeResponse(AgentResponse agentResponse) {
        String sb = agentResponse.getId() + "," +
                agentResponse.getValue();
        return sb.getBytes(defaultCharset);
    }


    static public AgentRequest deserializeRequest(byte[] bytes) {
        String body = new String(bytes, defaultCharset);
        String[] bodyArray = body.split(",");
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setId(Long.parseLong(bodyArray[0]));
        agentRequest.setInterfaceName(bodyArray[1]);
        agentRequest.setMethod(bodyArray[2]);
        agentRequest.setParameter(bodyArray[3]);
        agentRequest.setParameterTypesString(bodyArray[4]);
        return agentRequest;
    }

    static public AgentResponse deserializeResponse(byte[] bytes) {
        String body = new String(bytes, defaultCharset);
        String[] bodyArray = body.split(",");
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setId(Long.parseLong(bodyArray[0]));
        agentResponse.setValue(bodyArray[1]);
        return agentResponse;
    }

}
