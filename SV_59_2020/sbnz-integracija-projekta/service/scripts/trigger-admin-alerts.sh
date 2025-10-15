#!/usr/bin/env bash

# -----------------------------------------------------------------------------
# trigger-admin-alerts.sh
# -----------------------------------------------------------------------------
# Bash helper that uses wget + jq to exercise the rental approval + rating
# workflow and force Drools to emit admin notifications. By default it will run
# both a "negative" scenario (low rating to push a server below 30%) and a
# "positive" scenario (high rating to push a server above 70%).
#
# Requirements:
#   - Service backend running locally on http://localhost:8081
#   - jq (for JSON parsing) and wget installed
#   - Default seeded users (admin/admin123, demo/user123) present
#
# Usage:
#   chmod +x trigger-admin-alerts.sh
#   ./trigger-admin-alerts.sh [MODE]
#
# Modes:
#   positive  -> only send high ratings (defaults to service ID 4)
#   negative  -> only send low ratings (defaults to service ID 2)
#   both      -> run negative then positive (default when omitted)
#
# Environment overrides (optional):
#   BASE_URL                 defaults to http://localhost:8081
#   USER_USERNAME            defaults to demo
#   USER_PASSWORD            defaults to user123
#   ADMIN_USERNAME           defaults to admin
#   ADMIN_PASSWORD           defaults to admin123
#   POSITIVE_SERVICE_ID      defaults to 4
#   NEGATIVE_SERVICE_ID      defaults to 2
#   POSITIVE_ITERATIONS      defaults to 2 (number of rentals/ratings)
#   NEGATIVE_ITERATIONS      defaults to 3
#   POSITIVE_SCORE           defaults to 5
#   NEGATIVE_SCORE           defaults to 1
#   RENTAL_DURATION_DAYS     defaults to 7
#   RENTAL_PURPOSE           defaults to "Automated alert rehearsal"
#
# The script prints a concise log for every API call so you can correlate the
# backend console output with the actions taken here. Open the admin dashboard
# websocket view while running this script to observe live notifications.
# -----------------------------------------------------------------------------

set -euo pipefail

check_dependency() {
  local bin=$1
  if ! command -v "$bin" >/dev/null 2>&1; then
    echo "[error] Missing dependency: $bin" >&2
    exit 1
  fi
}

check_dependency wget
check_dependency jq

BASE_URL=${BASE_URL:-http://localhost:8081}
USER_USERNAME=${USER_USERNAME:-demo}
USER_PASSWORD=${USER_PASSWORD:-user123}
ADMIN_USERNAME=${ADMIN_USERNAME:-admin}
ADMIN_PASSWORD=${ADMIN_PASSWORD:-admin123}
POSITIVE_SERVICE_ID=${POSITIVE_SERVICE_ID:-4}
NEGATIVE_SERVICE_ID=${NEGATIVE_SERVICE_ID:-2}
POSITIVE_ITERATIONS=${POSITIVE_ITERATIONS:-2}
NEGATIVE_ITERATIONS=${NEGATIVE_ITERATIONS:-3}
POSITIVE_SCORE=${POSITIVE_SCORE:-5}
NEGATIVE_SCORE=${NEGATIVE_SCORE:-1}
RENTAL_DURATION_DAYS=${RENTAL_DURATION_DAYS:-7}
RENTAL_PURPOSE=${RENTAL_PURPOSE:-"Automated alert rehearsal"}

MODE=${1:-both}

info() {
  printf '[info] %s\n' "$*"
}

warn() {
  printf '[warn] %s\n' "$*" >&2
}

http_post() {
  local url=$1
  local body=${2:-}
  shift 2 || true
  local headers=("Content-Type: application/json" "Accept: application/json" "$@")
  local wget_args=(--quiet --output-document=- --method=POST)
  for header in "${headers[@]}"; do
    [[ -z "$header" ]] && continue
    wget_args+=(--header "$header")
  done
  if [[ -n "$body" ]]; then
    wget_args+=(--body-data "$body")
  fi
  wget "${wget_args[@]}" "$url"
}

http_get() {
  local url=$1
  shift || true
  local headers=("Accept: application/json" "$@")
  local wget_args=(--quiet --output-document=- --method=GET)
  for header in "${headers[@]}"; do
    [[ -z "$header" ]] && continue
    wget_args+=(--header "$header")
  done
  wget "${wget_args[@]}" "$url"
}

build_rental_payload() {
  local service_id=$1
  local purpose=$2
  local duration=$3
  jq -n --arg serviceOfferingId "$service_id" \
        --arg purpose "$purpose" \
        --arg durationDays "$duration" \
        '{serviceOfferingId: ($serviceOfferingId|tonumber), purpose: $purpose, durationDays: ($durationDays|tonumber)}'
}

build_rating_payload() {
  local score=$1
  jq -n --arg score "$score" '{score: ($score|tonumber)}'
}

login() {
  local username=$1
  local password=$2
  local response
  response=$(http_post "$BASE_URL/api/auth/login" "$(jq -n --arg u "$username" --arg p "$password" '{username: $u, password: $p}')")
  echo "$response" | jq -r '.token'
}

create_rental() {
  local token=$1
  local service_id=$2
  local payload=$3
  local response
  response=$(http_post "$BASE_URL/api/rentals" "$payload" "Authorization: Bearer $token")
  local rental_id
  rental_id=$(echo "$response" | jq -r '.id')
  if [[ "$rental_id" == "null" || -z "$rental_id" ]]; then
    echo "$response" >&2
    warn "Failed to create rental"
    exit 1
  fi
  local service_label
  service_label=$(echo "$response" | jq -r '(.serviceName // "?") + " (" + (.providerName // "?") + ")"')
  info "Created rental $rental_id for $service_label"
  echo "$rental_id"
}

approve_rental() {
  local token=$1
  local rental_id=$2
  local response
  response=$(http_post "$BASE_URL/api/admin/rentals/$rental_id/approve" '' "Authorization: Bearer $token")
  local service_label status_label
  service_label=$(echo "$response" | jq -r '.serviceName // "?"')
  status_label=$(echo "$response" | jq -r '.status // "unknown"')
  info "Approved rental $rental_id for $service_label -> status $status_label"
}

rate_rental() {
  local token=$1
  local rental_id=$2
  local payload=$3
  local score_label=$4
  local response
  response=$(http_post "$BASE_URL/api/rentals/$rental_id/rating" "$payload" "Authorization: Bearer $token")
  local rating
  rating=$(echo "$response" | jq -r '.rating // "?"')
  info "Rated rental $rental_id with $score_label (backend stored: $rating)"
}

run_scenario() {
  local label=$1
  local iterations=$2
  local service_id=$3
  local score=$4
  local purpose_suffix=$5
  info "--- Scenario: $label ($iterations iteration(s)) ---"
  for ((i=1; i<=iterations; i++)); do
    info "Iteration $i/$iterations"
    local rental_payload rating_payload rental_id iteration_purpose
    iteration_purpose="$RENTAL_PURPOSE [$label #$i]"
    rental_payload=$(build_rental_payload "$service_id" "$iteration_purpose" "$RENTAL_DURATION_DAYS")
    rental_id=$(create_rental "$USER_TOKEN" "$service_id" "$rental_payload")
    approve_rental "$ADMIN_TOKEN" "$rental_id"
    rating_payload=$(build_rating_payload "$score")
    rate_rental "$USER_TOKEN" "$rental_id" "$rating_payload" "$label score=$score"
  done
}

info "Logging in as user $USER_USERNAME"
USER_TOKEN=$(login "$USER_USERNAME" "$USER_PASSWORD")

info "Logging in as admin $ADMIN_USERNAME"
ADMIN_TOKEN=$(login "$ADMIN_USERNAME" "$ADMIN_PASSWORD")

case "$MODE" in
  positive)
    run_scenario "positive" "$POSITIVE_ITERATIONS" "$POSITIVE_SERVICE_ID" "$POSITIVE_SCORE" "POSITIVE"
    ;;
  negative)
    run_scenario "negative" "$NEGATIVE_ITERATIONS" "$NEGATIVE_SERVICE_ID" "$NEGATIVE_SCORE" "NEGATIVE"
    ;;
  both)
    run_scenario "negative" "$NEGATIVE_ITERATIONS" "$NEGATIVE_SERVICE_ID" "$NEGATIVE_SCORE" "NEGATIVE"
    run_scenario "positive" "$POSITIVE_ITERATIONS" "$POSITIVE_SERVICE_ID" "$POSITIVE_SCORE" "POSITIVE"
    ;;
  *)
    warn "Unknown mode '$MODE'. Supported: positive | negative | both"
    exit 1
    ;;
esac

info "Script finished. Check backend logs and the admin dashboard for new notifications."
