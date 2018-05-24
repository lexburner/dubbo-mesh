//package com.alibaba.dubbo.performance.demo.agent;
//
//import com.alibaba.dubbo.performance.demo.agent.dubbo.provider.RpcClient;
//import com.alibaba.dubbo.performance.demo.agent.loadbalance.RoundRobinLoadBalance;
//import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
//import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
//import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
//import org.asynchttpclient.AsyncHttpClient;
//import org.asynchttpclient.DefaultAsyncHttpClientConfig;
//import org.asynchttpclient.Dsl;
//import org.asynchttpclient.ListenableFuture;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.async.DeferredResult;
//
//import static org.asynchttpclient.Dsl.*;
//
////@RestController
//public class HelloAsyncController {
//
//    private Logger logger = LoggerFactory.getLogger(HelloAsyncController.class);
//
//    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
//
//    private RpcClient rpcClient = new RpcClient();
//    RoundRobinLoadBalance loadBalance = new RoundRobinLoadBalance();
//    private Object lock = new Object();
//    private AsyncHttpClient asyncHttpClient;
//
//    HelloAsyncController() {
//        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder();
//        builder.setKeepAlive(true).setMaxConnectionsPerHost(10);
//        asyncHttpClient = asyncHttpClient(builder);
//        System.out.println(asyncHttpClient.getConfig().getMaxConnectionsPerHost());
//        System.out.println(asyncHttpClient.getConfig().getMaxConnections());
//        System.out.println(asyncHttpClient.getConfig().getIoThreadsCount());
//    }
//
//    @RequestMapping(value = "")
//    public Object invoke(@RequestParam("interface") String interfaceName,
//                         @RequestParam("method") String method,
//                         @RequestParam("parameterTypesString") String parameterTypesString,
//                         @RequestParam("parameter") String parameter) throws Exception {
//        String type = System.getProperty("type");   // 获取type参数
//        if ("consumer".equals(type)) {
//            return consumer(interfaceName, method, parameterTypesString, parameter);
//        } else if ("provider".equals(type)) {
//            return provider(interfaceName, method, parameterTypesString, parameter);
//        } else {
//            return "Environment variable type is needed to set to provider or consumer.";
//        }
//    }
//
//    public byte[] provider(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
//
//        Object result = rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
//        return (byte[]) result;
//    }
//
//    public DeferredResult<ResponseEntity> consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
//
//        if (null == loadBalance.getEndpoints()) {
//            synchronized (lock) {
//                if (null == loadBalance.getEndpoints()) {
//                    loadBalance.setEndpoints(registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService"));
//                }
//            }
//        }
//
//        // 简单的负载均衡，随机取一个
//        Endpoint endpoint = loadBalance.select(null);
//
//        String url = "http://" + endpoint.getHost() + ":" + endpoint.getPort();
//
//        org.asynchttpclient.ProviderAgentRpcRequest request = org.asynchttpclient.Dsl.post(url)
//                .addFormParam("interface", interfaceName)
//                .addFormParam("method", method)
//                .addFormParam("parameterTypesString", parameterTypesString)
//                .addFormParam("parameter", parameter)
//                .build();
//        ListenableFuture<org.asynchttpclient.Response> responseFuture = asyncHttpClient.executeRequest(request);
//        DeferredResult<ResponseEntity> result = new DeferredResult<>();
//        Runnable callback = () -> {
//            try {
//                // 检查返回值是否正确,如果不正确返回500。有以下原因可能导致返回值不对:
//                // 1. agent解析dubbo返回数据不对
//                // 2. agent没有把request和dubbo的response对应起来
//                String value = responseFuture.get().getResponseBody();
//                result.setResult(new ResponseEntity<>(Integer.valueOf(value), HttpStatus.OK));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//        responseFuture.addListener(callback, null);
//        return result;
//    }
//}
