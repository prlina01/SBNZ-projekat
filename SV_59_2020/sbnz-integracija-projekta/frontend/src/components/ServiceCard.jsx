import PropTypes from 'prop-types';
import { humanizeEnum, formatCurrency } from '../utils/formatters.js';

const ServiceCard = ({ service }) => {
  const {
    name,
    provider,
    purpose,
    cpuPerformance,
    vcpuCount,
    gpuModel,
    gpuVram,
    ram,
    storageCapacity,
    storageType,
    pricePerHour,
    pricePerMonth,
    region,
    highAvailability,
    ecoFriendly,
    ddosProtection,
  } = service;

  const hourlyPrice = typeof pricePerHour === 'number' ? pricePerHour : 0;
  const monthlyPrice = typeof pricePerMonth === 'number' ? pricePerMonth : hourlyPrice * 24 * 30;
  const prettyPurpose = humanizeEnum(purpose);
  const prettyCpu = humanizeEnum(cpuPerformance);
  const prettyGpu = gpuModel && gpuModel !== 'NONE' ? humanizeEnum(gpuModel) : 'No GPU';
  const gpuLabel = gpuVram && gpuModel && gpuModel !== 'NONE' ? `${prettyGpu} • ${gpuVram}GB VRAM` : prettyGpu;
  const computeLabel = [prettyCpu, vcpuCount ? `${vcpuCount} vCPU` : null].filter(Boolean).join(' • ');
  const storageLabel = [storageCapacity ? `${storageCapacity} GB` : null, humanizeEnum(storageType)].filter(Boolean).join(' ');
  const memoryLabel = ram ? `${ram} GB RAM` : 'Memory configurable';
  const regionLabel = humanizeEnum(region) || 'Global';

  return (
    <article className="service-card">
      <header className="service-card__header">
        <div>
          <h3>{name}</h3>
          <span className="service-card__provider">{provider}</span>
        </div>
        <span className="service-card__badge">{prettyPurpose}</span>
      </header>
      <dl className="service-card__meta">
        <div>
          <dt>Compute</dt>
          <dd>{computeLabel}</dd>
        </div>
        <div>
          <dt>GPU</dt>
          <dd>{gpuLabel}</dd>
        </div>
        <div>
          <dt>Memory</dt>
          <dd>{memoryLabel}</dd>
        </div>
        <div>
          <dt>Storage</dt>
          <dd>{storageLabel}</dd>
        </div>
        <div>
          <dt>Region</dt>
          <dd>{regionLabel}</dd>
        </div>
      </dl>
      <footer className="service-card__footer">
        <div className="service-card__pricing">
          <span className="service-card__price">{formatCurrency(hourlyPrice)} / hour</span>
          <span className="service-card__price service-card__price--muted">{formatCurrency(monthlyPrice)} / month</span>
        </div>
        <div className="service-card__flags">
          {highAvailability && <span className="service-card__flag">HA</span>}
          {ecoFriendly && <span className="service-card__flag">Eco</span>}
          {ddosProtection && <span className="service-card__flag">DDoS</span>}
        </div>
      </footer>
    </article>
  );
};

ServiceCard.propTypes = {
  service: PropTypes.shape({
    id: PropTypes.number,
    name: PropTypes.string.isRequired,
    provider: PropTypes.string.isRequired,
    purpose: PropTypes.string.isRequired,
    cpuPerformance: PropTypes.string.isRequired,
    vcpuCount: PropTypes.number.isRequired,
    gpuModel: PropTypes.string.isRequired,
    gpuVram: PropTypes.number,
    ram: PropTypes.number.isRequired,
    storageCapacity: PropTypes.number.isRequired,
    storageType: PropTypes.string.isRequired,
    pricePerHour: PropTypes.number.isRequired,
    pricePerMonth: PropTypes.number.isRequired,
    region: PropTypes.string.isRequired,
    highAvailability: PropTypes.bool,
    ecoFriendly: PropTypes.bool,
    ddosProtection: PropTypes.bool,
  }).isRequired,
};

export default ServiceCard;
