#!/usr/bin/env bash

cd $(git rev-parse --show-toplevel)

python3 -m pip install setuptools wheel pytest
python3 -m pip install -r ./server/app/requirenments.txt

python3 --version