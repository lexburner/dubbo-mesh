package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import java.io.Serializable;

public class AgentResponse implements Serializable {
    private String value;
    private long id;
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}