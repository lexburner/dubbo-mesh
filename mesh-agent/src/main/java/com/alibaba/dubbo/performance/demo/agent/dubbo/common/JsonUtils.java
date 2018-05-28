package com.alibaba.dubbo.performance.demo.agent.dubbo.common;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ken.lj
 * @date 02/04/2018
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    /**
     * 序列化
     *
     * @param obj
     * @param writer
     * @throws IOException
     */
    public static void writeObject(Object obj, PrintWriter writer) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.write(obj);
        out.writeTo(writer);
        out.close(); // for reuse SerializeWriter buf
        writer.println();
        writer.flush();
    }

    /**
     * 字符串专用序列化
     * @param param
     * @param writer
     * @throws IOException
     */
    public static void writeString(String param,PrintWriter writer) throws IOException{
        if(param!=null){
            writer.println(new StringBuffer().append("\"").append(param).append("\""));
            writer.flush();
        }else {
            writer.println("null");
            writer.flush();
        }
    }

    /**
     * 反序列化
     *
     * @param b
     * @param writer
     */
    public static void writeBytes(byte[] b, PrintWriter writer) {
        writer.print(new String(b));
        writer.flush();
    }
}
