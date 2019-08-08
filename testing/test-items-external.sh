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

FILES="-f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-thrive-local.yml -f $HERE/docker-compose-stats.yml"

docker-compose $FILES up --build thrive-dependencies stats-dependencies

$ROOT_DIR/gradlew :test-items:bootRun &
ITEMS_PID=$!

$TEST_WORKSPACE/wait-for-dep.sh localhost:8085

set +e
python3 $HERE/items_suite.py

EXIT=$?

#todo document this
if [ "$#" -gt 0 ]; then
    read  -rsn1 -p "Press any key to continue";
fi

kill $ITEMS_PID
docker-compose $FILES rm -sf stats

rm -rf $TEST_WORKSPACE

exit $EXIT