#!/bin/bash

if [[ "$1" == "consumer" ]]; then
  echo "Starting consumer service..."
  nohup java -jar \
        -Xms2G \
        -Xmx2G \
        -Dlogs.dir=/root/logs \
        /root/dists/mesh-consumer.jar \
        > /root/logs/consumer.out 2>&1 &
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider service..."
  nohup java -jar \
        -Xms1G \
        -Xmx1G \
        -Ddubbo.protocol.port=20889 \
        -Ddubbo.application.qos.enable=false \
        -Dlogs.dir=/root/logs \
        /root/dists/mesh-provider.jar \
        > /root/logs/provider.out 2>&1 &
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider service..."
  nohup java -jar \
        -Xms2G \
        -Xmx2G \
        -Ddubbo.protocol.port=20890 \
        -Ddubbo.application.qos.enable=false \
        -Dlogs.dir=/root/logs \
        /root/dists/mesh-provider.jar \
        > /root/logs/provider.out 2>&1 &
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider service..."
  nohup java -jar \
        -Xms3G \
        -Xmx3G \
        -Ddubbo.protocol.port=20891 \
        -Ddubbo.application.qos.enable=false \
        -Dlogs.dir=/root/logs \
        /root/dists/mesh-provider.jar \
        > /root/logs/provider.out 2>&1 &
else
  echo "Unrecognized arguments, exit."
  exit 1
fi

if [ -x "$(command -v start-agent.sh)" ]; then
  start-agent.sh "$@"
fi
