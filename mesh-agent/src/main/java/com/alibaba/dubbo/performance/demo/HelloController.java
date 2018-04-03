package com.alibaba.dubbo.performance.demo;

import com.alibaba.dubbo.performance.demo.dubbo.RpcClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {

    private RpcClient rpcClient = new RpcClient();

    @RequestMapping(value = "/provider")
    public byte[] provider(@RequestParam("service") String service,
                           @RequestParam("method") String method,
                           @RequestParam("ptypes") String ptypes,
                           @RequestParam("args") String args) throws Exception {
        // 部署在拿到method和name，生成符合Dubbo格式的字节码，去调用Dubbo Provider，拿到返回值，再通过Http协议返回给agent
        Object result = rpcClient.hello(args);
        return (byte[]) result;
    }

    @RequestMapping(value = "/consumer")
    public String consumer(@RequestParam("service") String service,
                           @RequestParam("method") String method,
                           @RequestParam("ptypes") String ptypes,
                           @RequestParam("args") String args ) throws Exception {
        // consumer调用部署在consumer上的agent的该方法

        String url = "http://127.0.0.1:9000/provider";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("service",service));
        urlParameters.add(new BasicNameValuePair("method",method));
        urlParameters.add(new BasicNameValuePair("ptypes",ptypes));
        urlParameters.add(new BasicNameValuePair("args",args));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        int contentLength = (int) entity.getContentLength();

        byte[] responseBuffer = new byte[contentLength];
        entity.getContent().read(responseBuffer);

        return new String(responseBuffer);

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
    }

    @RequestMapping(value = "/test")
    public byte[] test(){
        byte[] bytes = new byte[]{1,2,3,4,5};
        return bytes;
    }
}
