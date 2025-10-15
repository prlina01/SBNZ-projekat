#!/usr/bin/env python3
"""trigger_admin_alerts.py

Utility helper that walks through the rental lifecycle (rent -> admin approve ->
customer rating) so Drools emits fresh performance reports. It mirrors the
shell helper but uses Python's requests library for clearer feedback.

Examples
--------
    # Positive-only scenario (default server ids match seeded catalog data)
    python trigger_admin_alerts.py --mode positive

    # Run both positive and negative scenarios against a remote service
    BASE_URL=http://api.example.com python trigger_admin_alerts.py --mode both

Prerequisites
-------------
* Python 3.9+
* `requests` library (install with ``pip install requests``)
* Backend running (defaults to http://localhost:8081)
* Seeded demo accounts: user ``demo`` (password ``user123``) and admin
  ``admin`` (password ``admin123``)
"""

from __future__ import annotations

import argparse
import os
import sys
import time
from dataclasses import dataclass
from typing import Dict, Optional

try:
    import requests
except ImportError as exc:  # pragma: no cover - dependency hint
    print("[error] Missing dependency: requests (pip install requests)", file=sys.stderr)
    raise SystemExit(1) from exc


DEFAULT_BASE_URL = os.environ.get("BASE_URL", "http://localhost:8081")
DEFAULT_USER_USERNAME = os.environ.get("USER_USERNAME", "demo")
DEFAULT_USER_PASSWORD = os.environ.get("USER_PASSWORD", "user123")
DEFAULT_ADMIN_USERNAME = os.environ.get("ADMIN_USERNAME", "admin")
DEFAULT_ADMIN_PASSWORD = os.environ.get("ADMIN_PASSWORD", "admin123")
DEFAULT_POSITIVE_SERVICE_ID = int(os.environ.get("POSITIVE_SERVICE_ID", 4))
DEFAULT_NEGATIVE_SERVICE_ID = int(os.environ.get("NEGATIVE_SERVICE_ID", 2))
DEFAULT_POSITIVE_ITERATIONS = int(os.environ.get("POSITIVE_ITERATIONS", 2))
DEFAULT_NEGATIVE_ITERATIONS = int(os.environ.get("NEGATIVE_ITERATIONS", 3))
DEFAULT_POSITIVE_SCORE = int(os.environ.get("POSITIVE_SCORE", 5))
DEFAULT_NEGATIVE_SCORE = int(os.environ.get("NEGATIVE_SCORE", 1))
DEFAULT_RENTAL_DURATION = int(os.environ.get("RENTAL_DURATION_DAYS", 7))
DEFAULT_PURPOSE = os.environ.get("RENTAL_PURPOSE", "Automated alert rehearsal")
DEFAULT_PAUSE = float(os.environ.get("STEP_PAUSE_SECONDS", 0.25))
DEFAULT_TIMEOUT = int(os.environ.get("HTTP_TIMEOUT_SECONDS", 10))


@dataclass
class Tokens:
    user: str
    admin: str


class ApiClient:
    def __init__(self, base_url: str, timeout: int) -> None:
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout
        self.session = requests.Session()
        self.session.headers.update({"Accept": "application/json"})

    def login(self, username: str, password: str) -> str:
        payload = {"username": username, "password": password}
        response = self.session.post(
            f"{self.base_url}/api/auth/login",
            json=payload,
            timeout=self.timeout,
        )
        _raise_for_status(response, "login")
        token = response.json().get("token")
        if not token:
            raise RuntimeError("Login succeeded but no token was returned")
        return token

    def create_rental(self, token: str, service_id: int, purpose: str, duration_days: int) -> Dict:
        payload = {
            "serviceOfferingId": service_id,
            "purpose": purpose,
            "durationDays": duration_days,
        }
        response = self.session.post(
            f"{self.base_url}/api/rentals",
            json=payload,
            headers={"Authorization": f"Bearer {token}"},
            timeout=self.timeout,
        )
        _raise_for_status(response, "create rental")
        return response.json()

    def approve_rental(self, token: str, rental_id: int) -> Dict:
        response = self.session.post(
            f"{self.base_url}/api/admin/rentals/{rental_id}/approve",
            headers={"Authorization": f"Bearer {token}"},
            timeout=self.timeout,
        )
        _raise_for_status(response, "approve rental")
        return response.json()

    def rate_rental(self, token: str, rental_id: int, score: int) -> Dict:
        payload = {"score": score}
        response = self.session.post(
            f"{self.base_url}/api/rentals/{rental_id}/rating",
            json=payload,
            headers={"Authorization": f"Bearer {token}"},
            timeout=self.timeout,
        )
        _raise_for_status(response, "rate rental")
        return response.json()


def _raise_for_status(response: requests.Response, label: str) -> None:
    try:
        response.raise_for_status()
    except requests.HTTPError as exc:  # pragma: no cover - simple wrapper
        details: Optional[str]
        try:
            details = response.json()
        except ValueError:
            details = response.text
        print(f"[error] Failed to {label}: {details}", file=sys.stderr)
        raise SystemExit(1) from exc


def log_info(message: str) -> None:
    print(f"[info] {message}")


def log_warn(message: str) -> None:
    print(f"[warn] {message}", file=sys.stderr)


@dataclass
class Scenario:
    label: str
    iterations: int
    service_id: int
    score: int


def run_scenario(
    client: ApiClient,
    tokens: Tokens,
    scenario: Scenario,
    purpose_template: str,
    duration_days: int,
    pause_seconds: float,
) -> None:
    log_info(f"--- Scenario: {scenario.label} ({scenario.iterations} iteration(s)) ---")
    for index in range(1, scenario.iterations + 1):
        log_info(f"Iteration {index}/{scenario.iterations}")
        purpose = f"{purpose_template} [{scenario.label.upper()} #{index}]"
        rental = client.create_rental(tokens.user, scenario.service_id, purpose, duration_days)
        rental_id = rental.get("id")
        service_name = rental.get("serviceName") or rental.get("serviceOfferingId")
        log_info(f"Created rental {rental_id} for {service_name}")

        approved = client.approve_rental(tokens.admin, rental_id)
        log_info(
            f"Approved rental {rental_id} -> status {approved.get('status')} "
            f"(service {approved.get('serviceName')})"
        )

        rated = client.rate_rental(tokens.user, rental_id, scenario.score)
        log_info(f"Rated rental {rental_id} with score {scenario.score} (stored rating: {rated.get('rating')})")

        if pause_seconds:
            time.sleep(pause_seconds)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Trigger Drools performance alerts by simulating rentals")
    parser.add_argument("--base-url", default=DEFAULT_BASE_URL, help="Service base URL (default: %(default)s)")
    parser.add_argument("--user-username", default=DEFAULT_USER_USERNAME, help="Customer username")
    parser.add_argument("--user-password", default=DEFAULT_USER_PASSWORD, help="Customer password")
    parser.add_argument("--admin-username", default=DEFAULT_ADMIN_USERNAME, help="Admin username")
    parser.add_argument("--admin-password", default=DEFAULT_ADMIN_PASSWORD, help="Admin password")
    parser.add_argument("--mode", choices=["positive", "negative", "both"], default="both",
                        help="Which scenario(s) to run")
    parser.add_argument("--positive-service-id", type=int, default=DEFAULT_POSITIVE_SERVICE_ID,
                        help="Service offering id for positive scenario")
    parser.add_argument("--negative-service-id", type=int, default=DEFAULT_NEGATIVE_SERVICE_ID,
                        help="Service offering id for negative scenario")
    parser.add_argument("--positive-iterations", type=int, default=DEFAULT_POSITIVE_ITERATIONS,
                        help="Iterations for positive scenario")
    parser.add_argument("--negative-iterations", type=int, default=DEFAULT_NEGATIVE_ITERATIONS,
                        help="Iterations for negative scenario")
    parser.add_argument("--positive-score", type=int, default=DEFAULT_POSITIVE_SCORE,
                        help="Rating score to push performance high")
    parser.add_argument("--negative-score", type=int, default=DEFAULT_NEGATIVE_SCORE,
                        help="Rating score to push performance low")
    parser.add_argument("--rental-duration", type=int, default=DEFAULT_RENTAL_DURATION,
                        help="Rental duration (days)")
    parser.add_argument("--purpose", default=DEFAULT_PURPOSE,
                        help="Base rental purpose for logging")
    parser.add_argument("--pause", type=float, default=DEFAULT_PAUSE,
                        help="Pause (seconds) between iterations to let rules fire")
    parser.add_argument("--timeout", type=int, default=DEFAULT_TIMEOUT,
                        help="HTTP timeout in seconds")
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    log_info(f"Using service base URL: {args.base_url}")

    client = ApiClient(args.base_url, args.timeout)
    log_info(f"Logging in as user {args.user_username}")
    user_token = client.login(args.user_username, args.user_password)
    log_info(f"Logging in as admin {args.admin_username}")
    admin_token = client.login(args.admin_username, args.admin_password)

    tokens = Tokens(user=user_token, admin=admin_token)

    scenarios = []
    if args.mode in ("negative", "both"):
        scenarios.append(Scenario("negative", args.negative_iterations, args.negative_service_id, args.negative_score))
    if args.mode in ("positive", "both"):
        scenarios.append(Scenario("positive", args.positive_iterations, args.positive_service_id, args.positive_score))

    if not scenarios:
        log_warn("No scenarios selected; exiting")
        return

    for scenario in scenarios:
        run_scenario(client, tokens, scenario, args.purpose, args.rental_duration, args.pause)

    log_info("Done. Check backend logs and admin dashboard for fresh alerts.")


if __name__ == "__main__":
    main()
