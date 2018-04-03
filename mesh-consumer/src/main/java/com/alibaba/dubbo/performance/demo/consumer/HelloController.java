package com.alibaba.dubbo.performance.demo.consumer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class HelloController {
    private Logger logger = LoggerFactory.getLogger(HelloController.class);
    private Random random = new Random();

    @RequestMapping(value = "/invoke")
    public String invoke() throws Exception {
        String str = String.valueOf(random.nextInt(500));

        String url = "http://127.0.0.1:20000";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("interface","com.alibaba.dubbo.performance.demo.provider.IHelloService"));
        urlParameters.add(new BasicNameValuePair("method","hash"));
        urlParameters.add(new BasicNameValuePair("parameterTypesString","Ljava/lang/String;"));
        urlParameters.add(new BasicNameValuePair("parameter",str));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        logger.info("Sending 'POST' request to URL : " + url);
        logger.info("Response Code : " + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // 检查dubbo返回的值是否和计算出的hash值一样
        // 如果不一样，说明没有把request和response对应起来
        return "str = " + str + "; hash = " + str.hashCode() + "; dubbo return " + result.toString();
    }
}
