package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.codec.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author 徐靖峰
 * Date 2018-06-04
 */
public class TestFastjson {

    public static void main(String[] args) throws Exception{
        for(int j=0;j<100;j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                RpcInvocation invocation = new RpcInvocation();
                invocation.setMethodName("HelloService");
                invocation.setAttachment("path", "moe.cnkirito.Hello.class");
                invocation.setParameterTypes("java/lang/string;");    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
                try {
                    JsonUtils.writeString("===================1234271896421963219===================1234271896421963219===================", writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                invocation.setArguments(out.toByteArray());

                DubboRpcEncoder dubboRpcEncoder = new DubboRpcEncoder();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dubboRpcEncoder.encodeRequestDataString(bos, invocation);
                byte[] bytes = bos.toByteArray();
            }
            System.out.println("cost " + (System.currentTimeMillis() - start) + " ms");
        }
    }

}
