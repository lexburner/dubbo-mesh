package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class AgentRequest implements Serializable {
    private static AtomicLong atomicLong = new AtomicLong();
    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;
    private long id;

    public AgentRequest() {
        id = atomicLong.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public void setParameterTypesString(String parameterTypesString) {
        this.parameterTypesString = parameterTypesString;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public void setId(long id) {
        this.id = id;
    }
}