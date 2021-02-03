#!/usr/bin/env bash

mkdir -p tmp
rm -rf tmp/*
mkdir -p tmp/usr/lib/qms/server/
cp -r ../app tmp/usr/lib/qms/server
cp -r ../../proto tmp/usr/lib/qms/

docker build . --network=host

rm -rf tmp