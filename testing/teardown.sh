#!/usr/bin/env bash

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..
TEST_WORKSPACE=$HERE/workspace

export ROOT_PROJECT=$ROOT_DIR

FILES="-f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-thrive-local.yml"

docker-compose $FILES rm -sf