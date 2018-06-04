package com.alibaba.dubbo.performance.demo.agent.rpc;

import java.io.Serializable;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public class DefaultRequest implements Request,Serializable {

    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;
    private long requestId;

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public void setParameterTypesString(String parameterTypesString) {
        this.parameterTypesString = parameterTypesString;
    }

    @Override
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}
