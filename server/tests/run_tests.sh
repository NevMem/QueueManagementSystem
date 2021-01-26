#!/usr/bin/env bash


DB_URL="postgresql+asyncpg://postgres:postgres@localhost:5432/postgres" python3 -m pytest ./server/tests

