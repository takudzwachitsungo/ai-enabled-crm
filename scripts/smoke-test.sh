#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
USER_NAME="${APP_USER:-local-dev}"
USER_PASS="${APP_PASS:-local-dev-pass}"
TENANT="${TENANT_ID:-tenant-demo}"

printf "[1/3] Checking health endpoint...\n"
curl -sS -u "$USER_NAME:$USER_PASS" "$BASE_URL/api/health" | grep -q 'UP'
printf "Health check passed.\n"

printf "[2/3] Creating a lead...\n"
CREATE_PAYLOAD='{"fullName":"Jane Doe","email":"jane@example.com"}'
CREATE_RES=$(curl -sS -u "$USER_NAME:$USER_PASS" -X POST "$BASE_URL/api/v1/leads" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: $TENANT" \
  -d "$CREATE_PAYLOAD")

echo "$CREATE_RES" | grep -q '"id"'
printf "Lead creation passed.\n"

printf "[3/3] Listing leads...\n"
LIST_RES=$(curl -sS -u "$USER_NAME:$USER_PASS" -X GET "$BASE_URL/api/v1/leads" \
  -H "X-Tenant-Id: $TENANT")

echo "$LIST_RES" | grep -q 'jane@example.com'
printf "Lead listing passed.\n"

printf "All smoke tests passed.\n"
