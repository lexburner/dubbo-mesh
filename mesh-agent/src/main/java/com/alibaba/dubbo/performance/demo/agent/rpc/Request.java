package com.alibaba.dubbo.performance.demo.agent.rpc;

/**
 * @author 徐靖峰
 * Date 2018-05-25
 */
public interface Request {

    String getInterfaceName();

    /**
     * service method name
     *
     * @return
     */
    String getMethod();

    /**
     * service method param desc (sign)
     *
     * @return
     */
    String getParameterTypesString();

    /**
     * service method param
     *
     * @return
     */
//    Object[] getArgs();
    String getParameter();

    /**
     * request id
     *
     * @return
     */
    long getRequestId();
}
