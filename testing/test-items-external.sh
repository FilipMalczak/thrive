#!/usr/bin/env bash

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..
TEST_WORKSPACE=$HERE/workspace

mkdir -p $TEST_WORKSPACE

$ROOT_DIR/gradlew build generateDockerfile

python3 -m pip install -r $HERE/requirements.txt

wget https://raw.githubusercontent.com/dadarek/docker-wait-for-dependencies/master/entrypoint.sh -O $TEST_WORKSPACE/wait-for-dep.sh

chmod +x $TEST_WORKSPACE/wait-for-dep.sh

export ROOT_PROJECT=$ROOT_DIR

docker-compose -f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-thrive-local.yml -f $HERE/docker-compose-items.yml up --build thrive-dependencies

$ROOT_DIR/gradlew bootRun &
ITEMS_PID=$!

$TEST_WORKSPACE/wait-for-dep.sh localhost:8085

set +e
python3 $HERE/items_suite.py

EXIT=$?

kill $ITEMS_PID
docker-compose -f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-items.yml rm -sf
docker volume prune -f
rm -rf $TEST_WORKSPACE

exit $EXIT