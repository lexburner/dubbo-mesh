#!/bin/bash

if [[ "$1" == "consumer" ]]; then
  echo "Starting consumer service..."
  java -jar -Xms2G -Xmx2G -Dlogs.dir=/root/logs /root/dists/mesh-consumer.jar
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider service..."
  java -jar -Xms1G -Xmx1G -Ddubbo.protocol.port=20889 -Dlogs.dir=/root/logs /root/dists/mesh-provider.jar
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider service..."
  java -jar -Xms2G -Xmx2G -Ddubbo.protocol.port=20890 -Dlogs.dir=/root/logs /root/dists/mesh-provider.jar
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider service..."
  java -jar -Xms3G -Xmx3G  -Ddubbo.protocol.port=20891 -Dlogs.dir=/root/logs /root/dists/mesh-provider.jar
else
  echo "Unrecognized arguments, exit."
  exit 1
fi

if [ -x "$(command -v start-agent.sh)" ]; then
  start-agent.sh "$@"
fi
