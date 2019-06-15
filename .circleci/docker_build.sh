#!/usr/bin/env bash
# THIS SCRIPT ASSUMES THAT JAR FILES WERE ALREADY GENERATED, PROBABLY BY ./gradlew clean build
# AS WELL AS THE ACCOMPANYING TASKS (listDockerizedProjectsDirectories, generateDockerfile,
# writeVersion) WERE ALREADY EXECUTED

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

for NAME in $(cat $ROOT_DIR/build/dockerizedProjects.txt)
do
    docker build $ROOT_DIR/$NAME -t filipmalczak/$NAME:$VERSION
done