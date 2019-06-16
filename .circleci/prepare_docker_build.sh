#!/usr/bin/env bash

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

set -ex

$ROOT_DIR/gradlew listDockerizedProjectsDirectories

for NAME in $(cat $ROOT_DIR/build/dockerizedProjects.txt)
do
    $ROOT_DIR/gradlew :$NAME:generateDockerfile :$NAME:writeVersion
    VERSION=$(cat $ROOT_DIR/$NAME/build/generated/meta/version.txt)
done