#!/usr/bin/env bash

cd $(git rev-parse --show-toplevel)

sudo apt update
sudo apt -y install python3.9 python3-pip python3-pytest
python3 -m pip install setuptools wheel pytest
python3 -m pip install -r ./server/app/requirenments.txt

python3 --version