#!/usr/bin/env bash

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

set -e
echo "Logging in to DockerHub with username $DOCKER_USERNAME"
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
set -x

ADMIN_VERSION=$(cat $ROOT_DIR/thrive-admin/build/thrive/metadata/version.txt)
DOCS_VERSION=$(cat $ROOT_DIR/thrive-docs/build/thrive/metadata/version.txt)
GATEWAY_VERSION=$(cat $ROOT_DIR/thrive-gateway/build/thrive/metadata/version.txt)

docker push thriveframework/thrive-admin:$ADMIN_VERSION
docker push thriveframework/thrive-docs:$DOCS_VERSION
docker push thriveframework/thrive-gateway:$GATEWAY_VERSION