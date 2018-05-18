package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client.AgentClient;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

//@RestController
public class HelloNettyController {

    private Logger logger = LoggerFactory.getLogger(HelloNettyController.class);

    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));

    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private AgentClient agentClient = new AgentClient();

    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        String type = System.getProperty("type");   // 获取type参数
        if ("consumer".equals(type)) {
            return consumer(interfaceName, method, parameterTypesString, parameter);
        }
        return "no response";
    }

    public Integer consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
        }
        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        String s = (String) agentClient.invoke(interfaceName, method, parameterTypesString, parameter, endpoint);
        return Integer.valueOf(s);
    }
}
