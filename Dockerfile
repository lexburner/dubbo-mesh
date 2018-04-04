# Builder container
FROM registry.cn-hangzhou.aliyuncs.com/tianchi4-docker/debian-jdk8-devel AS builder

COPY . /root/workspace
WORKDIR /root/workspace
RUN set -ex && mvn clean package


# Runner container
FROM registry.cn-hangzhou.aliyuncs.com/tianchi4-docker/debian-jdk8

COPY --from=builder /root/workspace/mesh-provider/target/mesh-provider-1.0-SNAPSHOT.jar /root/dists/mesh-provider.jar
COPY --from=builder /root/workspace/mesh-consumer/target/mesh-consumer-1.0-SNAPSHOT.jar /root/dists/mesh-consumer.jar
COPY --from=builder /root/workspace/mesh-agent/target/mesh-agent-1.0-SNAPSHOT.jar /root/dists/mesh-agent.jar

COPY docker-entrypoint.sh /usr/local/bin
COPY start-agent.sh /usr/local/bin

RUN set -ex \
 && mkdir -p /root/logs

ENTRYPOINT ["docker-entrypoint.sh"]
