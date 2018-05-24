### 项目注意事项

netty 的版本修改为 4.1.25.Final，注意有两个 pom 文件，确认内层的 netty 版本为最新版本

修改 etcd 客户端的版本，去掉原先的 exclude
```
    <dependency>
        <groupId>com.coreos</groupId>
        <artifactId>jetcd-core</artifactId>
        <version>0.0.2</version>
    </dependency>
```


### 项目结构介绍

consumer -> consumer-agent          固定请求方法为 http，不能修改
consumer-agent -> provider-agent    自定义协议和通信方式，目前使用 netty 通信，封装了 RpcCallbackFuture
provider-agent -> provider          固定 dubbo 协议，目前使用 netty，封装了 RpcCallbackFuture

com.alibaba.dubbo.performance.demo.agent.dubbo.consumer 实现了 consumer-agent 的 http 服务器
提供了两个实现，HelloNettyController 实现了非阻塞返回结果的 springmvc http 服务器；ConsumerAgentHttpServer 实现了非阻塞返回结果的 netty http 服务器
但由于未知 bug，理论上性能更好的 netty http 服务器目前实际测试结果 qps 低于 springmvc http 服务器，所以保留了这两个实现

com.alibaba.dubbo.performance.demo.agent.dubbo.agent 包下存放的是 consumer-agent 和 provider-agent 的主要实现

com.alibaba.dubbo.performance.demo.agent.dubbo.agent.client 实现了 consumer-agent，使用 netty 封装了 ConsumerAgentMvcClient 和 ConsumerAgentNettyClient 客户端
当使用 springmvc 作为 comsumer-agent 的 http 服务器接收来自 consumer 的请求时，使用 ConsumerAgentMvcClient 作为客户端
当使用 netty 作为 comsumer-agent 的 http 服务器接收来自 consumer 的请求时，使用 ConsumerAgentMvcClient 作为客户端
由于未知 bug，理论上性能更好的 netty http 服务器目前实际测试结果 qps 低于 springmvc http 服务器，所以保留了这两个实现

com.alibaba.dubbo.performance.demo.agent.dubbo.agent.server 实现了 provider-agent
使用 netty 作为服务器，接收来自 consumer-agent 的请求

com.alibaba.dubbo.performance.demo.agent.dubbo.provider 实现了 provider-agent 向 provider 发送请求的客户端
RpcAsyncClient 实现了 dubbo 协议，是调用的入口

整个链路全部使用非阻塞通信

### TODO

1. 解决使用 netty 作为 http 服务器后反而出现 qps 下降 1000 的问题，是不是 http 的逻辑处理有问题，还是 netty 使用方式不对
2. 整个项目链路已经纯异步话，结果返回 future，使用 future.addListener 的方式处理返回值，但 qps 仅仅达到 3200，和预估值 4000 有差距，这个差距的根源未找到
3. 长连接复用存疑，netty 的线程模型中，一个客户端的长连接对应一个 nioEventLoop 线程，consumer-agent 和一个 provider-agent 实例目前维护了 1 个长连接，导致
provider-agent 只会实例化一个 handler 使用一个 eventLoop 处理 io 事件，这是不是导致 qps 达不到 4000 的根源？维护多个连接我也试过了，效果不理想，但还是记录下这个疑问
4. 在 consumer-agent 这儿实现 dubbo 协议，provider-agent 作为一个流量转发服务器，这样少了一个自定义协议编解码的过程，可能会提高 qps
