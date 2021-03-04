#!/bin/sh

if ! [[ -x "$(command -v docker)" ]]; then
  echo "Error: docker is not installed. See https://docs.docker.com/install/ for details.'" >&2
  exit 1
fi

echo "Packaging project..."
./mvnw clean package -DskipTests=true

echo "Preparing files..."
rm -f docker/*.war
cp score-http/target/*.war docker

echo "Building docker image..."
cd docker
docker build --no-cache -f Dockerfile -t oagi1docker/srt-http-gateway:2.0.0.3 .
cd ..

echo "Done."
