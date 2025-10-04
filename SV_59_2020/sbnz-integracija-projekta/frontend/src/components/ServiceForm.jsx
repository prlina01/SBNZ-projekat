import PropTypes from 'prop-types';
import { useEffect, useMemo, useState } from 'react';
import { PURPOSES, CPU_PERFORMANCE, GPU_MODELS, STORAGE_TYPES, REGIONS } from '../utils/enums.js';
import { humanizeEnum } from '../utils/formatters.js';

const defaultForm = {
  name: '',
  provider: '',
  purpose: PURPOSES[0],
  cpuPerformance: CPU_PERFORMANCE[1],
  vcpuCount: '4',
  gpuModel: GPU_MODELS[0],
  gpuVram: '0',
  ram: '16',
  storageCapacity: '256',
  storageType: STORAGE_TYPES[0],
  encryptedStorage: true,
  networkBandwidth: '100',
  ddosProtection: true,
  highAvailability: false,
  region: REGIONS[0],
  ecoFriendly: false,
  dedicatedCpu: false,
  autoscalingCapable: false,
  managedService: false,
  replicationSupport: false,
  multiZone: false,
  onPremiseAvailable: false,
  hybridDeployment: false,
  energyEfficient: false,
  storageIops: '0',
  pricePerHour: '5',
  pricePerMonth: '350',
};

const mapInitialValues = (initialValues) => {
  if (!initialValues) {
    return defaultForm;
  }
  return {
    name: initialValues.name ?? '',
    provider: initialValues.provider ?? '',
    purpose: initialValues.purpose ?? PURPOSES[0],
    cpuPerformance: initialValues.cpuPerformance ?? CPU_PERFORMANCE[1],
    vcpuCount: String(initialValues.vcpuCount ?? ''),
    gpuModel: initialValues.gpuModel ?? GPU_MODELS[0],
    gpuVram: String(initialValues.gpuVram ?? 0),
    ram: String(initialValues.ram ?? ''),
    storageCapacity: String(initialValues.storageCapacity ?? ''),
    storageType: initialValues.storageType ?? STORAGE_TYPES[0],
    encryptedStorage: Boolean(initialValues.encryptedStorage),
    networkBandwidth: String(initialValues.networkBandwidth ?? ''),
    ddosProtection: Boolean(initialValues.ddosProtection),
    highAvailability: Boolean(initialValues.highAvailability),
    region: initialValues.region ?? REGIONS[0],
    ecoFriendly: Boolean(initialValues.ecoFriendly),
    dedicatedCpu: Boolean(initialValues.dedicatedCpu),
    autoscalingCapable: Boolean(initialValues.autoscalingCapable),
    managedService: Boolean(initialValues.managedService),
    replicationSupport: Boolean(initialValues.replicationSupport),
    multiZone: Boolean(initialValues.multiZone),
    onPremiseAvailable: Boolean(initialValues.onPremiseAvailable),
    hybridDeployment: Boolean(initialValues.hybridDeployment),
    energyEfficient: Boolean(initialValues.energyEfficient),
    storageIops: String(initialValues.storageIops ?? 0),
    pricePerHour: String(initialValues.pricePerHour ?? ''),
    pricePerMonth: String(initialValues.pricePerMonth ?? ''),
  };
};

const ServiceForm = ({ mode, initialValues, onSubmit, onCancel, isSubmitting }) => {
  const [form, setForm] = useState(defaultForm);

  useEffect(() => {
    setForm(mapInitialValues(initialValues));
  }, [initialValues]);

  const isEditMode = mode === 'edit';

  const isGpuDisabled = useMemo(() => form.gpuModel === 'NONE', [form.gpuModel]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleNumberChange = (event) => {
    const { name, value } = event.target;
    if (/^-?\d*(\.\d*)?$/.test(value)) {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleCheckbox = (event) => {
    const { name, checked } = event.target;
    setForm((prev) => ({ ...prev, [name]: checked }));
  };

  const parseInteger = (value, fallback = 0) => {
    const parsed = parseInt(value, 10);
    return Number.isNaN(parsed) ? fallback : parsed;
  };

  const parseFloatValue = (value, fallback = 0) => {
    const parsed = parseFloat(value);
    return Number.isNaN(parsed) ? fallback : parsed;
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const payload = {
      name: form.name.trim(),
      provider: form.provider.trim(),
      purpose: form.purpose,
      cpuPerformance: form.cpuPerformance,
      vcpuCount: parseInteger(form.vcpuCount, 1),
      gpuModel: form.gpuModel,
      gpuVram: parseInteger(form.gpuVram, 0),
      ram: parseInteger(form.ram, 1),
      storageCapacity: parseInteger(form.storageCapacity, 1),
      storageType: form.storageType,
      encryptedStorage: form.encryptedStorage,
      networkBandwidth: parseInteger(form.networkBandwidth, 0),
      ddosProtection: form.ddosProtection,
      highAvailability: form.highAvailability,
      region: form.region,
      ecoFriendly: form.ecoFriendly,
      dedicatedCpu: form.dedicatedCpu,
      autoscalingCapable: form.autoscalingCapable,
      managedService: form.managedService,
      replicationSupport: form.replicationSupport,
      multiZone: form.multiZone,
      onPremiseAvailable: form.onPremiseAvailable,
      hybridDeployment: form.hybridDeployment,
      energyEfficient: form.energyEfficient,
      storageIops: parseInteger(form.storageIops, 0),
      pricePerHour: parseFloatValue(form.pricePerHour, 0),
      pricePerMonth: parseFloatValue(form.pricePerMonth, 0),
    };

    onSubmit(payload);
  };

  return (
    <form className="service-form" onSubmit={handleSubmit}>
      <header className="service-form__header">
        <h2>{isEditMode ? 'Edit service offering' : 'Create new service offering'}</h2>
        {isEditMode && (
          <button
            type="button"
            className="link-button"
            disabled={isSubmitting}
            onClick={onCancel}
          >
            Cancel edit
          </button>
        )}
      </header>

      <div className="form-grid">
        <label className="form-field">
          <span>Name</span>
          <input
            type="text"
            name="name"
            value={form.name}
            onChange={handleChange}
            disabled={isSubmitting}
            required
          />
        </label>
        <label className="form-field">
          <span>Provider</span>
          <input
            type="text"
            name="provider"
            value={form.provider}
            onChange={handleChange}
            disabled={isSubmitting}
            required
          />
        </label>
        <label className="form-field">
          <span>Purpose</span>
          <select
            name="purpose"
            value={form.purpose}
            onChange={handleChange}
            disabled={isSubmitting}
            required
          >
            {PURPOSES.map((option) => (
              <option key={option} value={option}>{humanizeEnum(option)}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span>CPU performance</span>
          <select
            name="cpuPerformance"
            value={form.cpuPerformance}
            onChange={handleChange}
            disabled={isSubmitting}
            required
          >
            {CPU_PERFORMANCE.map((option) => (
              <option key={option} value={option}>{humanizeEnum(option)}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span>vCPU count</span>
          <input
            type="text"
            inputMode="numeric"
            name="vcpuCount"
            value={form.vcpuCount}
            onChange={handleNumberChange}
            disabled={isSubmitting}
            required
          />
        </label>
        <label className="form-field">
          <span>GPU model</span>
          <select
            name="gpuModel"
            value={form.gpuModel}
            onChange={handleChange}
            disabled={isSubmitting}
          >
            {GPU_MODELS.map((option) => (
              <option key={option} value={option}>{humanizeEnum(option)}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span>GPU VRAM (GB)</span>
          <input
            type="text"
            inputMode="numeric"
            name="gpuVram"
            value={form.gpuVram}
            onChange={handleNumberChange}
            disabled={isSubmitting || isGpuDisabled}
          />
        </label>
        <label className="form-field">
          <span>RAM (GB)</span>
          <input
            type="text"
            inputMode="numeric"
            name="ram"
            value={form.ram}
            onChange={handleNumberChange}
            disabled={isSubmitting}
            required
          />
        </label>
        <label className="form-field">
          <span>Storage (GB)</span>
          <input
            type="text"
            inputMode="numeric"
            name="storageCapacity"
            value={form.storageCapacity}
            onChange={handleNumberChange}
            disabled={isSubmitting}
            required
          />
        </label>
        <label className="form-field">
          <span>Storage type</span>
          <select
            name="storageType"
            value={form.storageType}
            onChange={handleChange}
            disabled={isSubmitting}
            required
          >
            {STORAGE_TYPES.map((option) => (
              <option key={option} value={option}>{humanizeEnum(option)}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span>Network bandwidth (Mbps)</span>
          <input
            type="text"
            inputMode="numeric"
            name="networkBandwidth"
            value={form.networkBandwidth}
            onChange={handleNumberChange}
            disabled={isSubmitting}
          />
        </label>
        <label className="form-field">
          <span>Region</span>
          <select
            name="region"
            value={form.region}
            onChange={handleChange}
            disabled={isSubmitting}
            required
          >
            {REGIONS.map((option) => (
              <option key={option} value={option}>{humanizeEnum(option)}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span>Storage IOPS</span>
          <input
            type="text"
            inputMode="numeric"
            name="storageIops"
            value={form.storageIops}
            onChange={handleNumberChange}
            disabled={isSubmitting}
          />
        </label>
      </div>

      <div className="form-switches">
        <label className="switch-field">
          <input
            type="checkbox"
            name="encryptedStorage"
            checked={form.encryptedStorage}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Encrypted storage</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="ddosProtection"
            checked={form.ddosProtection}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>DDoS protection</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="highAvailability"
            checked={form.highAvailability}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>High availability</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="ecoFriendly"
            checked={form.ecoFriendly}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Eco-friendly</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="dedicatedCpu"
            checked={form.dedicatedCpu}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Dedicated CPU</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="autoscalingCapable"
            checked={form.autoscalingCapable}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Autoscaling capable</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="managedService"
            checked={form.managedService}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Managed service</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="replicationSupport"
            checked={form.replicationSupport}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Replication support</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="multiZone"
            checked={form.multiZone}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Multi-zone</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="onPremiseAvailable"
            checked={form.onPremiseAvailable}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>On-premise available</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="hybridDeployment"
            checked={form.hybridDeployment}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Hybrid deployment</span>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            name="energyEfficient"
            checked={form.energyEfficient}
            onChange={handleCheckbox}
            disabled={isSubmitting}
          />
          <span>Energy efficient</span>
        </label>
      </div>

      <div className="form-grid">
        <label className="form-field">
          <span>Price per hour (USD)</span>
          <input
            type="text"
            inputMode="decimal"
            name="pricePerHour"
            value={form.pricePerHour}
            onChange={handleNumberChange}
            disabled={isSubmitting}
            required
          />
        </label>
        <label className="form-field">
          <span>Price per month (USD)</span>
          <input
            type="text"
            inputMode="decimal"
            name="pricePerMonth"
            value={form.pricePerMonth}
            onChange={handleNumberChange}
            disabled={isSubmitting}
            required
          />
        </label>
      </div>

      <footer className="service-form__footer">
        <button type="submit" className="btn btn--primary" disabled={isSubmitting}>
          {isSubmitting ? 'Saving…' : isEditMode ? 'Save changes' : 'Create service'}
        </button>
      </footer>
    </form>
  );
};

ServiceForm.propTypes = {
  mode: PropTypes.oneOf(['create', 'edit']).isRequired,
  initialValues: PropTypes.shape({
    id: PropTypes.number,
    name: PropTypes.string,
    provider: PropTypes.string,
    purpose: PropTypes.string,
    cpuPerformance: PropTypes.string,
    vcpuCount: PropTypes.number,
    gpuModel: PropTypes.string,
    gpuVram: PropTypes.number,
    ram: PropTypes.number,
    storageCapacity: PropTypes.number,
    storageType: PropTypes.string,
    encryptedStorage: PropTypes.bool,
    networkBandwidth: PropTypes.number,
    ddosProtection: PropTypes.bool,
    highAvailability: PropTypes.bool,
    region: PropTypes.string,
    ecoFriendly: PropTypes.bool,
    dedicatedCpu: PropTypes.bool,
    autoscalingCapable: PropTypes.bool,
    managedService: PropTypes.bool,
    replicationSupport: PropTypes.bool,
    multiZone: PropTypes.bool,
    onPremiseAvailable: PropTypes.bool,
    hybridDeployment: PropTypes.bool,
    energyEfficient: PropTypes.bool,
    storageIops: PropTypes.number,
    pricePerHour: PropTypes.number,
    pricePerMonth: PropTypes.number,
  }),
  onSubmit: PropTypes.func.isRequired,
  onCancel: PropTypes.func,
  isSubmitting: PropTypes.bool,
};

ServiceForm.defaultProps = {
  initialValues: null,
  onCancel: () => {},
  isSubmitting: false,
};

export default ServiceForm;
