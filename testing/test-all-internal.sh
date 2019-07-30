#!/usr/bin/env bash

#TODO these scripts share a lot of code - refactor

set -ex

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

$ROOT_DIR/gradlew build generateDockerfile

python -m pip install -r $HERE/requirements.txt

export ROOT_PROJECT=$ROOT_DIR

FILES="-f $HERE/docker-compose-thrive.yml -f $HERE/docker-compose-items.yml -f $HERE/docker-compose-stats.yml"

docker-compose $FILES up --build thrive-dependencies
docker-compose $FILES up --build items-dependencies
docker-compose $FILES up --build stats-dependencies

set +e
python $HERE/items_suite.py

EXIT=$?

if [ $EXIT -gt 0 ]; then
    docker-compose $FILES logs items
    echo "***************************************************************"
    echo ""
    echo ""
    echo ""
    echo "***************************************************************"
    docker-compose $FILES logs stats
fi

#todo document this
if [ "$#" -gt 0 ]; then
    read  -rsn1 -p "Press any key to continue";
fi

docker-compose $FILES rm -sf
docker volume prune -f

exit $EXIT