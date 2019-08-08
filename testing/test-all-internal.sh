#!/usr/bin/env bash

#TODO these scripts share a lot of code - refactor

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

$ROOT_DIR/gradlew build generateDockerfile

export ROOT_PROJECT=$ROOT_DIR

COMMON_FILES="-f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-items.yml"
FILES="$COMMON_FILES -f $HERE/docker-compose-stats.yml"

docker-compose $FILES up thrive-dependencies
docker-compose $FILES up --build items-dependencies stats-dependencies

set +e
python3 $HERE/items_suite.py

EXIT=$?

#todo document this
if [ "$#" -gt 0 ]; then
    read  -rsn1 -p "Press any key to continue";
fi

docker-compose $FILES rm -sf items stats

exit $EXIT