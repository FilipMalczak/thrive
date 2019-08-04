#!/usr/bin/env bash

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..
TEST_WORKSPACE=$HERE/workspace

mkdir -p $TEST_WORKSPACE

$ROOT_DIR/gradlew build generateDockerfile

export ROOT_PROJECT=$ROOT_DIR

FILES="-f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-thrive-local.yml"

docker-compose $FILES up --build thrive-dependencies