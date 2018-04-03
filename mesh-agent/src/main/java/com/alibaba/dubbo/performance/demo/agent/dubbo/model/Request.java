package com.alibaba.dubbo.performance.demo.agent.dubbo.model;


import java.util.concurrent.atomic.AtomicLong;

public class Request {
    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private String interfaceName = "com.alibaba.dubbo.performance.demo.provider.IHelloService";
    private String methodName = "hash";
    private String dubboVersion = "2.6.0";
    private String version = "0.0.0";
    private String parameterTypesString = "Ljava/lang/String;";
    private Object[] args;
    private boolean twoWay = true;
    private boolean event = false;

    private Object mData;

    public Request(){
        id = atomicLong.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getDubboVersion() {
        return dubboVersion;
    }

    public void setDubboVersion(String dubboVersion) {
        this.dubboVersion = dubboVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public void setParameterTypesString(String parameterTypesString) {
        this.parameterTypesString = parameterTypesString;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object msg) {
        mData = msg;
    }

}
