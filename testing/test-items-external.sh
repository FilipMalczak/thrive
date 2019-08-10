#!/usr/bin/env bash

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..
TEST_WORKSPACE=$HERE/workspace

mkdir -p $TEST_WORKSPACE

$ROOT_DIR/gradlew build generateDockerfile

wget https://raw.githubusercontent.com/dadarek/docker-wait-for-dependencies/master/entrypoint.sh -O $TEST_WORKSPACE/wait-for-dep.sh

chmod +x $TEST_WORKSPACE/wait-for-dep.sh

export ROOT_PROJECT=$ROOT_DIR

COMMON_FILES="-f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-thrive-local.yml"
PROJECT="-p items-external"
FILES="$COMMON_FILES -f $HERE/docker-compose-stats.yml"

COMPOSE_FLAGS="$FILES $PROJECT"

docker-compose $COMPOSE_FLAGS up thrive-dependencies
docker-compose $COMPOSE_FLAGS up --build stats-dependencies

$ROOT_DIR/gradlew :test-items:bootRun &
ITEMS_PID=$!

$TEST_WORKSPACE/wait-for-dep.sh localhost:8085

set +e
python $HERE/items_suite.py

EXIT=$?

#todo document this
if [ "$#" -gt 0 ]; then
    read  -rsn1 -p "Press any key to continue";
fi

kill $ITEMS_PID
docker-compose $COMPOSE_FLAGS rm -sf stats

rm -rf $TEST_WORKSPACE
sleep 5

exit $EXIT