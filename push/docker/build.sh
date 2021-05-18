#!/usr/bin/env bash

mkdir -p tmp || exit 1
rm -rf tmp/* || exit 1
mkdir -p tmp/usr/lib/qms/push/ || exit 1
../app/gradelew build
cp -r ../app/push/build/libs/push* tmp/usr/lib/qms/push.jar || exit 1

docker build . --network=host -t cr.yandex/crp59su1rb652ia7ojk8/push:$(git rev-parse HEAD) || exit 1

rm -rf tmp