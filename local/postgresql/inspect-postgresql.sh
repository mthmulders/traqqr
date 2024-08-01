#!/usr/bin/env bash
set -euo pipefail

docker exec -it traqqr_postgresql_database psql -U postgres
