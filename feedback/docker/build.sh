#!/usr/bin/env bash

mkdir -p tmp || exit 1
rm -rf tmp/* || exit 1
mkdir -p tmp/usr/lib/qms/feedback/ || exit 1
cd ../app || exit 1
./gradlew build || exit 1
cp -r feedback/build/libs/feedback* ../docker/tmp/usr/lib/qms/feedback.jar || exit 1
cd ../docker || exit 1

docker build . --network=host -t cr.yandex/crp59su1rb652ia7ojk8/feedback:$(git rev-parse HEAD) || exit 1

rm -rf tmp