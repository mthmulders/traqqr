#!/usr/bin/env bash
set -euo pipefail

docker stop traqqr_postgresql_database
docker rm traqqr_postgresql_database