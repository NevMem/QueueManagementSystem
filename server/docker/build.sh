#!/usr/bin/env bash

mkdir -p tmp
rm -rf tmp/*
mkdir -p tmp/usr/lib/qms/server/
cp -r ../app tmp/usr/lib/qms/server
cp -r ../../proto tmp/usr/lib/qms/

docker build . --network=host -t cr.yandex/crp59su1rb652ia7ojk8/init:$(git rev-parse HEAD)

rm -rf tmp