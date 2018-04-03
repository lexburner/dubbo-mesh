package com.alibaba.dubbo.performance.demo.consumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {

    @RequestMapping(value = "/invoke")
    public String invoke() throws Exception {
        String methodName = "hello";
        String serviceName = "com.alibaba.performance.dubbomesh.provider.IHelloService";
        String paramTypes = "Ljava/lang/String;"; //目前dubbo端是强依赖java类型描述的实现，未做改造。
        String arguments = "123"; //TODO 序列化直接放在这一端，传byte数组


        String url = "http://127.0.0.1:9000/consumer";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.setHeader("HEADER_1","header1");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("service",serviceName));
        urlParameters.add(new BasicNameValuePair("method",methodName));
        urlParameters.add(new BasicNameValuePair("ptypes",paramTypes));
        urlParameters.add(new BasicNameValuePair("args",arguments));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

        return result.toString();
    }

    @RequestMapping(value = "/test")
    public String test() throws IOException {
        String url = "http://127.0.0.1:9000/test";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // post.setHeader("HEADER_1","header1");

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        int contentLength = (int) entity.getContentLength();

        byte[] responseBuffer = new byte[contentLength];
        entity.getContent().read(responseBuffer);


//        BufferedReader rd = new BufferedReader(
//                new InputStreamReader(response.getEntity().getContent()));
//
//        StringBuffer result = new StringBuffer();
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//
//        System.out.println(result.toString());
//
//        return result.toString();

        return "";


    }

}
