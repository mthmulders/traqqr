#!/usr/bin/env bash
set -euo pipefail

docker pull postgres:16-alpine

docker run --name traqqr_postgresql_database \
    -e POSTGRES_DB=postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -d \
    -p 5432:5432 \
    postgres:16-alpine
