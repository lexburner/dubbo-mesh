//package com.alibaba.dubbo.performance.demo.agent.dubbo.consumer.mvc;
//
//import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client.ConsumerAgentMvcClient;
//import com.alibaba.dubbo.performance.demo.agent.loadbalance.RoundRobinLoadBalance;
//import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
//import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
//import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class HelloNettyController {
//
//    private Logger logger = LoggerFactory.getLogger(HelloNettyController.class);
//
//    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
//
//    RoundRobinLoadBalance loadBalance = new RoundRobinLoadBalance();
//    private Object lock = new Object();
//    private ConsumerAgentMvcClient agentClient = new ConsumerAgentMvcClient();
//
//    @RequestMapping(value = "")
//    public Object invoke(@RequestParam("interface") String interfaceName,
//                         @RequestParam("method") String method,
//                         @RequestParam("parameterTypesString") String parameterTypesString,
//                         @RequestParam("parameter") String parameter) throws Exception {
//        String type = System.getProperty("type");   // 获取type参数
//        if ("consumer".equals(type)) {
//            return consumer(interfaceName, method, parameterTypesString, parameter);
//        }
//        return "no response";
//    }
//
//    public Object consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
//
//        if (null == loadBalance.getEndpoints()) {
//            synchronized (lock) {
//                if (null == loadBalance.getEndpoints()) {
//                    loadBalance.setEndpoints(registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService"));
//                }
//            }
//        }
//        // 简单的负载均衡，随机取一个
//        Endpoint endpoint = loadBalance.select(null);
//        // 简单的负载均衡，随机取一个
//        return agentClient.invoke(interfaceName, method, parameterTypesString, parameter, endpoint);
//    }
//}
