package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 徐靖峰[OF2938]
 * company qianmi.com
 * Date 2018-05-23
 */
public class CommonThreadPool {

    public static ExecutorService executorService = Executors.newFixedThreadPool(200);
}
