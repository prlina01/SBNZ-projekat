import { useState } from 'react';
import { PURPOSES, CPU_PERFORMANCE, STORAGE_TYPES, REGIONS } from '../utils/enums.js';

const BOOLEAN_OPTIONS = [
  { value: true, label: 'Yes' },
  { value: false, label: 'No' },
];

const defaultFilters = {
  purpose: PURPOSES[0],
  cpuPerformance: CPU_PERFORMANCE[1],
  minVcpuCount: 4,
  gpuRequired: false,
  minGpuVram: 0,
  minRam: 16,
  minStorageCapacity: 256,
  storageType: STORAGE_TYPES[0],
  encryptedStorage: false,
  minNetworkBandwidth: 100,
  ddosProtection: false,
  highAvailability: false,
  region: REGIONS[0],
  ecoPriority: false,
  concurrentUsers: 500,
  budget: 'MEDIUM',
  rentalDuration: 30,
};

const RecommendationForm = ({ onSubmit, loading }) => {
  const [filters, setFilters] = useState(defaultFilters);

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    let parsed = value;

    if (type === 'number') {
      parsed = value === '' ? '' : Number(value);
    }

    if (name === 'gpuRequired' || name === 'encryptedStorage' || name === 'ddosProtection' || name === 'highAvailability' || name === 'ecoPriority') {
      parsed = value === 'true';
    }

    setFilters((prev) => ({
      ...prev,
      [name]: parsed,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      ...filters,
      minVcpuCount: Number(filters.minVcpuCount),
      minGpuVram: Number(filters.minGpuVram),
      minRam: Number(filters.minRam),
      minStorageCapacity: Number(filters.minStorageCapacity),
      minNetworkBandwidth: Number(filters.minNetworkBandwidth),
      concurrentUsers: Number(filters.concurrentUsers),
      rentalDuration: Number(filters.rentalDuration),
    };
    onSubmit(payload);
  };

  return (
    <form className="recommendation-form" onSubmit={handleSubmit}>
      <div className="recommendation-form__grid">
        <div className="recommendation-form__card">
          <h2>Workload profile</h2>
          <div className="recommendation-form__fields">
            <label>
              <span>Purpose</span>
              <select name="purpose" value={filters.purpose} onChange={handleChange} required>
                {PURPOSES.map((p) => (
                  <option key={p} value={p}>{p.replace('_', ' ')}</option>
                ))}
              </select>
            </label>
            <label>
              <span>Concurrent users</span>
              <input type="number" name="concurrentUsers" value={filters.concurrentUsers} onChange={handleChange} min="1" required />
            </label>
            <label>
              <span>Rental duration (days)</span>
              <input type="number" name="rentalDuration" value={filters.rentalDuration} onChange={handleChange} min="1" required />
            </label>
            <label>
              <span>Budget</span>
              <select name="budget" value={filters.budget} onChange={handleChange} required>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </label>
          </div>
        </div>

        <div className="recommendation-form__card">
          <h2>Compute requirements</h2>
          <div className="recommendation-form__fields">
            <label>
              <span>CPU performance</span>
              <select name="cpuPerformance" value={filters.cpuPerformance} onChange={handleChange} required>
                {CPU_PERFORMANCE.map((c) => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
            </label>
            <label>
              <span>Minimum vCPU</span>
              <input type="number" name="minVcpuCount" value={filters.minVcpuCount} onChange={handleChange} min="1" required />
            </label>
            <label>
              <span>Min RAM (GB)</span>
              <input type="number" name="minRam" value={filters.minRam} onChange={handleChange} min="1" required />
            </label>
            <label>
              <span>GPU required</span>
              <select name="gpuRequired" value={String(filters.gpuRequired)} onChange={handleChange} required>
                {BOOLEAN_OPTIONS.map((option) => (
                  <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                ))}
              </select>
            </label>
            <label>
              <span>Min GPU VRAM (GB)</span>
              <input type="number" name="minGpuVram" value={filters.minGpuVram} onChange={handleChange} min="0" required={filters.gpuRequired} disabled={!filters.gpuRequired} />
            </label>
          </div>
        </div>

        <div className="recommendation-form__card">
          <h2>Platform guarantees</h2>
          <div className="recommendation-form__fields">
            <label>
              <span>DDoS protection</span>
              <select name="ddosProtection" value={String(filters.ddosProtection)} onChange={handleChange} required>
                {BOOLEAN_OPTIONS.map((option) => (
                  <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                ))}
              </select>
            </label>
            <label>
              <span>High availability</span>
              <select name="highAvailability" value={String(filters.highAvailability)} onChange={handleChange} required>
                {BOOLEAN_OPTIONS.map((option) => (
                  <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                ))}
              </select>
            </label>
            <label>
              <span>Encrypted storage</span>
              <select name="encryptedStorage" value={String(filters.encryptedStorage)} onChange={handleChange} required>
                {BOOLEAN_OPTIONS.map((option) => (
                  <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                ))}
              </select>
            </label>
            <label>
              <span>Eco priority</span>
              <select name="ecoPriority" value={String(filters.ecoPriority)} onChange={handleChange} required>
                {BOOLEAN_OPTIONS.map((option) => (
                  <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                ))}
              </select>
            </label>
          </div>
        </div>

        <div className="recommendation-form__card">
          <h2>Storage & network</h2>
          <div className="recommendation-form__fields">
            <label>
              <span>Min storage (GB)</span>
              <input type="number" name="minStorageCapacity" value={filters.minStorageCapacity} onChange={handleChange} min="1" required />
            </label>
            <label>
              <span>Storage type</span>
              <select name="storageType" value={filters.storageType} onChange={handleChange} required>
                {STORAGE_TYPES.map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </label>
            <label>
              <span>Min bandwidth (Mbps)</span>
              <input type="number" name="minNetworkBandwidth" value={filters.minNetworkBandwidth} onChange={handleChange} min="1" required />
            </label>
            <label>
              <span>Region</span>
              <select name="region" value={filters.region} onChange={handleChange} required>
                {REGIONS.map((r) => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>
            </label>
          </div>
        </div>
      </div>

      <div className="recommendation-form__actions">
        <button type="submit" className="btn btn--primary" disabled={loading}>
          {loading ? 'Searching…' : 'Find Recommendations'}
        </button>
      </div>
    </form>
  );
};

export default RecommendationForm;
