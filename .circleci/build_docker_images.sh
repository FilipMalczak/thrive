#!/usr/bin/env bash
# THIS SCRIPT ASSUMES THAT JAR FILES WERE ALREADY GENERATED, PROBABLY BY ./gradlew clean build

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

$ROOT_DIR/gradlew listDockerizedProjectsDirectories

for NAME in $(cat $ROOT_DIR/build/dockerizedProjects.txt)
do
    $ROOT_DIR/gradlew :$NAME:generateDockerfile :$NAME:writeVersion
    VERSION=$(cat $ROOT_DIR/$NAME/build/generated/meta/version.txt)
    docker build $ROOT_DIR/$NAME -t filipmalczak/$NAME:$VERSION
done