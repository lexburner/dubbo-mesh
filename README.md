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