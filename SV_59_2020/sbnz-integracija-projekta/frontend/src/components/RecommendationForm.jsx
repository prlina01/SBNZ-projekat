import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import { PURPOSES, CPU_PERFORMANCE, STORAGE_TYPES, REGIONS } from '../utils/enums.js';

const BOOLEAN_OPTIONS = [
  { value: true, label: 'Yes' },
  { value: false, label: 'No' },
];

const BOOLEAN_FIELDS = new Set([
  'gpuRequired',
  'encryptedStorage',
  'ddosProtection',
  'highAvailability',
  'ecoPriority',
  'gdprRequired',
]);

const purposeOptions = ['ANY', ...PURPOSES];
const regionOptions = ['ANY', ...REGIONS];
const storageOptions = ['ANY', ...STORAGE_TYPES];

const defaultFilters = {
  purpose: 'ANY',
  cpuPerformance: CPU_PERFORMANCE[1],
  minVcpuCount: 2,
  gpuRequired: false,
  minGpuVram: 0,
  minRam: 4,
  minStorageCapacity: 0,
  storageType: 'ANY',
  encryptedStorage: false,
  minNetworkBandwidth: 100,
  ddosProtection: false,
  highAvailability: false,
  region: 'ANY',
  ecoPriority: false,
  concurrentUsers: 500,
  budget: 'MEDIUM',
  rentalDuration: 30,
  datasetSizeGb: 0,
  gdprRequired: false,
};

const RecommendationForm = ({ onSubmit, loading }) => {
  const [filters, setFilters] = useState(defaultFilters);
  const [showAdvanced, setShowAdvanced] = useState(false);

  const effectivePurpose = filters.purpose;
  const purposeAny = effectivePurpose === 'ANY' || effectivePurpose === null;
  const isMlTraining = effectivePurpose === 'ML_TRAINING';
  const isMlInference = effectivePurpose === 'ML_INFERENCE';
  const isDatabase = effectivePurpose === 'DATABASE';
  const isWebApp = effectivePurpose === 'WEB_APP';
  const isStreaming = effectivePurpose === 'STREAMING';
  const isAnalytics = effectivePurpose === 'DATA_ANALYTICS';

  const allowGpu = purposeAny || isMlTraining || isMlInference;
  const allowDataset = isMlTraining;
  const allowGdpr = isMlTraining || isMlInference;
  const allowHighAvailability = purposeAny || isMlTraining || isDatabase || isWebApp;
  const allowDdos = purposeAny || isMlTraining || isDatabase || isWebApp || isStreaming;
  const allowEncryption = purposeAny || isMlTraining || isDatabase;
  const allowEco = purposeAny || isStreaming;
  const allowStorage = purposeAny || isMlTraining || isAnalytics || isDatabase;
  const allowBandwidth = purposeAny || isMlTraining || isMlInference || isStreaming || isWebApp;

  useEffect(() => {
    setFilters((prev) => {
      const purpose = prev.purpose;
      const purposeAnyInner = purpose === 'ANY' || purpose === null;
      const allowGpuInner = purposeAnyInner || purpose === 'ML_TRAINING' || purpose === 'ML_INFERENCE';
      const allowDatasetInner = purpose === 'ML_TRAINING';
      const allowGdprInner = purpose === 'ML_TRAINING' || purpose === 'ML_INFERENCE';
      const allowHighAvailabilityInner = purposeAnyInner || purpose === 'ML_TRAINING' || purpose === 'DATABASE' || purpose === 'WEB_APP';
      const allowDdosInner = purposeAnyInner || purpose === 'ML_TRAINING' || purpose === 'DATABASE' || purpose === 'WEB_APP' || purpose === 'STREAMING';
      const allowEncryptionInner = purposeAnyInner || purpose === 'ML_TRAINING' || purpose === 'DATABASE';
    const allowEcoInner = purposeAnyInner || purpose === 'STREAMING';
    const allowStorageInner = purposeAnyInner || purpose === 'ML_TRAINING' || purpose === 'DATA_ANALYTICS' || purpose === 'DATABASE';

      let changed = false;
      const updated = { ...prev };

      if (!allowGpuInner && (prev.gpuRequired || prev.minGpuVram !== 0)) {
        updated.gpuRequired = false;
        updated.minGpuVram = 0;
        changed = true;
      }
      if (!allowDatasetInner && prev.datasetSizeGb !== 0) {
        updated.datasetSizeGb = 0;
        changed = true;
      }
      if (!allowGdprInner && prev.gdprRequired) {
        updated.gdprRequired = false;
        changed = true;
      }
      if (!allowHighAvailabilityInner && prev.highAvailability) {
        updated.highAvailability = false;
        changed = true;
      }
      if (!allowDdosInner && prev.ddosProtection) {
        updated.ddosProtection = false;
        changed = true;
      }
      if (!allowEncryptionInner && prev.encryptedStorage) {
        updated.encryptedStorage = false;
        changed = true;
      }
      if (!allowEcoInner && prev.ecoPriority) {
        updated.ecoPriority = false;
        changed = true;
      }
      if (!allowStorageInner && (prev.minStorageCapacity !== 0 || prev.storageType !== 'ANY')) {
        updated.minStorageCapacity = 0;
        updated.storageType = 'ANY';
        changed = true;
      }

      return changed ? updated : prev;
    });
  }, [filters.purpose]);

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    let parsed = value;

    if (type === 'number') {
      parsed = value === '' ? '' : Number(value);
    }

    if (BOOLEAN_FIELDS.has(name)) {
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
      datasetSizeGb: Number(filters.datasetSizeGb),
      gdprRequired: Boolean(filters.gdprRequired),
    };
    if (filters.purpose === 'ANY') {
      payload.purpose = null;
    }
    if (filters.region === 'ANY') {
      delete payload.region;
    }
    if (filters.storageType === 'ANY') {
      delete payload.storageType;
    }
    if (filters.minStorageCapacity === '' || Number(filters.minStorageCapacity) === 0) {
      payload.minStorageCapacity = 0;
    }
    onSubmit(payload, filters);
  };

  return (
    <form className="recommendation-form" onSubmit={handleSubmit}>
      <div className="recommendation-form__card">
        <h2>Core preferences</h2>
        <div className="recommendation-form__fields">
          <label>
            <span>Purpose</span>
            <select name="purpose" value={filters.purpose} onChange={handleChange} required>
              {purposeOptions.map((p) => (
                <option key={p} value={p}>{p === 'ANY' ? 'Any purpose' : p.replace('_', ' ')}</option>
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
          <label>
            <span>Region</span>
            <select name="region" value={filters.region} onChange={handleChange}>
              {regionOptions.map((r) => (
                <option key={r} value={r}>{r === 'ANY' ? 'Any region' : r}</option>
              ))}
            </select>
          </label>
        </div>
      </div>

      <div className="recommendation-form__toggle">
        <button type="button" className="btn btn--ghost" onClick={() => setShowAdvanced((prev) => !prev)}>
          {showAdvanced ? 'Hide advanced filters' : 'Show advanced filters'}
        </button>
        <span className="recommendation-form__toggle-note">
          {showAdvanced ? 'Only fields relevant to the selected purpose are displayed.' : 'Tune GPU, storage, and compliance options as needed.'}
        </span>
      </div>

      {showAdvanced && (
        <div className="recommendation-form__grid">
          {(allowDataset || allowGdpr) && (
            <div className="recommendation-form__card">
              <h2>Workload nuances</h2>
              <div className="recommendation-form__fields">
                {allowDataset && (
                  <label>
                    <span>Dataset size (GB)</span>
                    <input type="number" name="datasetSizeGb" value={filters.datasetSizeGb} onChange={handleChange} min="0" />
                  </label>
                )}
                {allowGdpr && (
                  <label>
                    <span>GDPR required</span>
                    <select name="gdprRequired" value={String(filters.gdprRequired)} onChange={handleChange}>
                      {BOOLEAN_OPTIONS.map((option) => (
                        <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                      ))}
                    </select>
                  </label>
                )}
              </div>
            </div>
          )}

          {(allowGpu || isAnalytics || isWebApp || isStreaming || isDatabase || purposeAny) && (
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
              </div>
            </div>
          )}

          {allowGpu && (
            <div className="recommendation-form__card">
              <h2>GPU acceleration</h2>
              <div className="recommendation-form__fields">
                <label>
                  <span>GPU required</span>
                  <select name="gpuRequired" value={String(filters.gpuRequired)} onChange={handleChange}>
                    {BOOLEAN_OPTIONS.map((option) => (
                      <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                    ))}
                  </select>
                </label>
                <label>
                  <span>Min GPU VRAM (GB)</span>
                  <input
                    type="number"
                    name="minGpuVram"
                    value={filters.minGpuVram}
                    onChange={handleChange}
                    min="0"
                    disabled={!filters.gpuRequired}
                    required={filters.gpuRequired}
                  />
                </label>
              </div>
            </div>
          )}

          {(allowStorage || allowBandwidth) && (
            <div className="recommendation-form__card">
              <h2>Storage &amp; network</h2>
              <div className="recommendation-form__fields">
                {allowStorage && (
                  <label>
                    <span>Min storage (GB)</span>
                    <input type="number" name="minStorageCapacity" value={filters.minStorageCapacity} onChange={handleChange} min="1" />
                  </label>
                )}
                {allowStorage && (
                  <label>
                    <span>Storage type</span>
                    <select name="storageType" value={filters.storageType} onChange={handleChange}>
                      {storageOptions.map((s) => (
                        <option key={s} value={s}>{s}</option>
                      ))}
                    </select>
                  </label>
                )}
                {allowBandwidth && (
                  <label>
                    <span>Min bandwidth (Mbps)</span>
                    <input type="number" name="minNetworkBandwidth" value={filters.minNetworkBandwidth} onChange={handleChange} min="1" />
                  </label>
                )}
              </div>
            </div>
          )}

          {(allowHighAvailability || allowDdos || allowEncryption || allowEco) && (
            <div className="recommendation-form__card">
              <h2>Platform safeguards</h2>
              <div className="recommendation-form__fields">
                {allowHighAvailability && (
                  <label>
                    <span>High availability</span>
                    <select name="highAvailability" value={String(filters.highAvailability)} onChange={handleChange}>
                      {BOOLEAN_OPTIONS.map((option) => (
                        <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                      ))}
                    </select>
                  </label>
                )}
                {allowDdos && (
                  <label>
                    <span>DDoS protection</span>
                    <select name="ddosProtection" value={String(filters.ddosProtection)} onChange={handleChange}>
                      {BOOLEAN_OPTIONS.map((option) => (
                        <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                      ))}
                    </select>
                  </label>
                )}
                {allowEncryption && (
                  <label>
                    <span>Encrypted storage</span>
                    <select name="encryptedStorage" value={String(filters.encryptedStorage)} onChange={handleChange}>
                      {BOOLEAN_OPTIONS.map((option) => (
                        <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                      ))}
                    </select>
                  </label>
                )}
                {allowEco && (
                  <label>
                    <span>Eco priority</span>
                    <select name="ecoPriority" value={String(filters.ecoPriority)} onChange={handleChange}>
                      {BOOLEAN_OPTIONS.map((option) => (
                        <option key={String(option.value)} value={String(option.value)}>{option.label}</option>
                      ))}
                    </select>
                  </label>
                )}
              </div>
            </div>
          )}
        </div>
      )}

      <div className="recommendation-form__actions">
        <button type="submit" className="btn btn--primary" disabled={loading}>
          {loading ? 'Searching…' : 'Find Recommendations'}
        </button>
      </div>
    </form>
  );
};

RecommendationForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  loading: PropTypes.bool,
};

RecommendationForm.defaultProps = {
  loading: false,
};

export default RecommendationForm;
