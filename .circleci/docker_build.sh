#!/usr/bin/env bash
# THIS SCRIPT ASSUMES THAT JAR FILES WERE ALREADY GENERATED, PROBABLY BY ./gradlew clean build
# AS WELL AS THE ACCOMPANYING TASKS (listDockerizedProjectsDirectories, generateDockerfile,
# writeVersion) WERE ALREADY EXECUTED

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

set -ex

for NAME in $(cat $ROOT_DIR/build/dockerizedProjects.txt)
do
    docker build -t filipmalczak/$NAME:$VERSION $ROOT_DIR/$NAME
done