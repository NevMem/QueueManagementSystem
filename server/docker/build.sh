#!/usr/bin/env bash

mkdir -p tmp || exit 1
rm -rf tmp/* || exit 1
mkdir -p tmp/usr/lib/qms/server/ || exit 1
cp -r ../app tmp/usr/lib/qms/server || exit 1
cp -r ../../proto tmp/usr/lib/qms/ || exit 1

docker build . --network=host -t cr.yandex/crp59su1rb652ia7ojk8/init:$(git rev-parse HEAD) || exit 1

rm -rf tmp