#!/usr/bin/env bash

ROOT_PROJECT=$PWD/../../..

pushd ${ROOT_PROJECT}
./gradlew :plugins:gradle:jar :tool:jar
popd

${ROOT_PROJECT}/gradlew --no-daemon --stacktrace humanStyleCheck
