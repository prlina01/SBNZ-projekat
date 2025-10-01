# Recommendation Engine Reference Scenarios

This note captures the current seed data loaded into the Drools knowledge base and provides ready-to-run request samples for the `/api/services/recommendations` endpoint. Use it as a quick reference when debugging scores, building demos, or validating future rule changes.

## Seeded server catalog

| ID | Name | Provider | Purpose | CPU | GPU | RAM (GB) | Storage | Bandwidth (Mbps) | Region | Eco | Price/month (€) | Notable traits |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Hetzner AX41 | Hetzner | ML_TRAINING | HIGH · 16 vCPU | NVIDIA_T4 · 16 GB | 128 | 2 TB NVME · encrypted | 1,000 | EU | ✅ | 950 | Strong ML baseline; full HA & DDoS; eco-friendly |
| 2 | Scaleway Stardust | Scaleway | WEB_APP | LOW · 2 vCPU | — | 4 | 50 GB SATA · unencrypted | 100 | EU | ❌ | 4.5 | Budget web host; no HA/DDoS, limited bandwidth |
| 3 | DigitalOcean Droplet | DigitalOcean | WEB_APP | MEDIUM · 8 vCPU | — | 16 | 320 GB NVME · encrypted | 400 | US | ✅ | 95 | Balanced web tier with HA & DDoS; eco-friendly |
| 4 | Vultr VC2 | Vultr | DATA_ANALYTICS | HIGH · 12 vCPU | — | 48 | 512 GB NVME · encrypted | 800 | US | ❌ | 180 | High-throughput analytics node; HA & DDoS |
| 5 | AWS M6i Large | AWS | DATABASE | HIGH · 32 vCPU | — | 128 | 1 TB NVME · encrypted | 2,000 | US | ✅ | 1,400 | Enterprise DB spine; HA/DDoS; premium tier |
| 6 | Google C2 Stream | Google Cloud | STREAMING | MEDIUM · 8 vCPU | NVIDIA_T4 · 16 GB | 32 | 500 GB NVME · encrypted | 1,200 | APAC | ✅ | 780 | Streaming with GPU assist; eco-friendly & HA |

> **Tip:** The same specifications are also persisted to the SQL catalog via `CatalogDataInitializer`, so the REST layer and the Drools knowledge base stay in sync after each restart (`spring.jpa.hibernate.ddl-auto=create-drop`).

## Sample recommendation requests

Each payload below is validated (`limit` must be ≥ 1) and can be posted with `Content-Type: application/json` to `POST /api/services/recommendations`. You may omit `limit` to fall back to the default of 10 results. If you are filling out the React form, every select/number field is required—including `purpose` and `concurrentUsers`—so the examples show complete payloads that map 1:1 with the UI. The region dropdown now includes an **Any region** choice; when selected, the request omits the `region` filter so cross-continent offers remain eligible.

### 1. GPU ready & eco-friendly (multiple matches)

Targets GPU-enabled nodes with sustainability and resilience requirements. Select **Any region** in the UI to surface both **Hetzner AX41** and **Google C2 Stream**; locking the dropdown to a specific geography (e.g., EU) narrows the list back down to providers operating there.

```json
{
  "purpose": "ML_TRAINING",
  "cpuPerformance": "MEDIUM",
  "minVcpuCount": 8,
  "gpuRequired": true,
  "minGpuVram": 16,
  "minRam": 32,
  "minStorageCapacity": 500,
  "storageType": "NVME",
  "encryptedStorage": true,
  "minNetworkBandwidth": 800,
  "concurrentUsers": 200,
  "ddosProtection": true,
  "highAvailability": true,
  "ecoPriority": true,
  "region": "EU",
  "budget": "HIGH",
  "rentalDuration": 180,
  "limit": 3
}
```

*Why it works*: Hetzner AX41 (EU) ships with an NVIDIA T4 GPU, ≥800 Mbps bandwidth, encrypted NVMe storage, and eco-friendly flags, perfectly matching the filters. Google C2 Stream mirrors those characteristics but is tagged for streaming workloads in APAC; switch the purpose selector to **Any purpose** and keep the region on **Any region** (or unset it in the API call) to keep it in contention—otherwise the stricter ML/EU combination trims the list to Hetzner alone.

### 2. Mission-critical database (single dominant match)

Tuned for a long-term, enterprise-grade primary database in the EU region. With the stricter geography filter, none of the seeded offerings fully satisfy the criteria—highlighting when to flip the frontend selector to **Any region** so you can still surface **AWS M6i Large** as the best-fit option.

```json
{
  "purpose": "DATABASE",
  "cpuPerformance": "HIGH",
  "minVcpuCount": 16,
  "minRam": 96,
  "minStorageCapacity": 800,
  "storageType": "NVME",
  "encryptedStorage": true,
  "ddosProtection": true,
  "highAvailability": true,
  "region": "EU",
  "budget": "HIGH",
  "rentalDuration": 365,
  "limit": 3
}
```

*Why it works*: With `region` locked to EU the response will be empty, because no seeded database node resides in that geography. Switch the selector to **Any region** (new in the UI) to let the engine consider AWS M6i Large, which still checks every other box—purpose, encryption, HA, NVMe, ≥800 GB storage, premium budget, and long-term rental boosts.

### 3. Ultra-low-latency EU inference (no matches)

Deliberately over-constrained to illustrate an empty result set. None of the seed servers are tagged for `ML_INFERENCE`, nor do they provide 24 GB GPU VRAM in the EU with ≥1.2 Gbps bandwidth.

```json
{
  "purpose": "ML_INFERENCE",
  "gpuRequired": true,
  "minGpuVram": 24,
  "minNetworkBandwidth": 1200,
  "region": "EU",
  "ecoPriority": true,
  "limit": 3
}
```

*Why it works*: Drools applies purpose alignment and GPU thresholds early. With no inference-optimized SKU in the catalog, every candidate stalls at baseline scoring, so the sorted result list is empty.

## Expected responses

- Responses are sorted by Drools score (descending) and tie-broken by monthly price.
- Each item extends `ServiceOfferingResponse`, so you receive normalized `matchScore` (0–100), a `highlights` list, and contextual `warnings`.
- For authenticated calls, loyalty bonuses can change ordering—e.g., a gold user with recent AWS rentals could further boost AWS listings.

### Snapshot of expected top hits

| Scenario | Expected services | Notes |
| --- | --- | --- |
| GPU ready & eco-friendly | Hetzner AX41, Google C2 Stream | Hetzner usually ranks first thanks to ML scenario bonuses; Google follows closely with streaming+eco boosts. |
| Mission-critical database | AWS M6i Large | Secondary candidates (e.g., Vultr VC2) stay well behind due to purpose mismatch and missing encryption bonuses. |
| Ultra-low-latency EU inference | *No results* | Frontend displays “No service found for your query.” |

## How to try it quickly

```bash
curl -k \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT>" \  # optional
  -d '{
        "gpuRequired": true,
        "minGpuVram": 16,
        "minNetworkBandwidth": 800,
        "encryptedStorage": true,
        "ddosProtection": true,
        "highAvailability": true,
        "ecoPriority": true,
        "limit": 3
      }' \
  https://localhost:8443/api/services/recommendations
```

Replace the payload with any of the scenarios above. If you test anonymously, the engine still works; you just skip loyalty adjustments.

---

*Maintainer reminder:* Update this document whenever you change `DroolsConfig` seeds, the SQL initializer, or `search.drl` scoring logic so demos stay authoritative.
