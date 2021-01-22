#!/usr/bin/env bash

cd $(git rev-parse --show-toplevel)

sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt update
sudo apt -y install postgresql-12 python3.9 python3-pip python3-pytest
python3.9 -m pip install setuptools wheel pytest
python3.9 -m pip install -r ./server/app/requirenments.txt
