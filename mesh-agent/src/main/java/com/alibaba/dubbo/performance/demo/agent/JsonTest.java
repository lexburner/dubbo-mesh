package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.common.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author 徐靖峰
 * Date 2018-05-28
 */
public class JsonTest {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos));
        JsonUtils.writeObject("2.0.1", writer);
        System.out.println(new String(bos.toByteArray()));

    }
}
