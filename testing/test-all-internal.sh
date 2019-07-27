#!/usr/bin/env bash

#TODO these scripts share a lot of code - refactor

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

$ROOT_DIR/gradlew build generateDockerfile

python -m pip install -r $HERE/requirements.txt

export ROOT_PROJECT=$ROOT_DIR

docker-compose -f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-items.yml up --build thrive-dependencies
docker-compose -f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-items.yml up --build items-dependencies

set +e
python $HERE/items_suite.py

EXIT=$?

docker-compose -f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-items.yml rm -sf
docker volume prune -f

exit $EXIT