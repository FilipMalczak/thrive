#!/usr/bin/env bash

HERE="$(realpath $(dirname "$0"))"
ROOT_DIR=$HERE/..

set -ex

$ROOT_DIR/gradlew listDockerizedProjectsDirectories