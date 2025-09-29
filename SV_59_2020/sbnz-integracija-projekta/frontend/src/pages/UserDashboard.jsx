import { useCallback, useEffect, useMemo, useState } from 'react';
import ServiceCard from '../components/ServiceCard.jsx';
import { fetchServices } from '../api/services.js';
import { PURPOSES } from '../utils/enums.js';
import { humanizeEnum } from '../utils/formatters.js';

const UserDashboard = () => {
  const [services, setServices] = useState([]);
  const [status, setStatus] = useState('loading');
  const [error, setError] = useState(null);
  const [purposeFilter, setPurposeFilter] = useState('ALL');

  const loadServices = useCallback(async () => {
    setStatus('loading');
    setError(null);
    try {
      const data = await fetchServices();
      setServices(data);
      setStatus('success');
    } catch (err) {
      const message = err?.response?.data?.message || 'Unable to load your catalog right now.';
      setError(message);
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  const filteredServices = useMemo(() => {
    if (purposeFilter === 'ALL') {
      return services;
    }
    return services.filter((service) => service.purpose === purposeFilter);
  }, [services, purposeFilter]);

  return (
    <div className="page dashboard-page">
      <header className="page__header">
        <div>
          <h1>Your CloudCraft workspace</h1>
          <p>Review matching infrastructure offers and pick the best fit for your workload.</p>
        </div>
        <div className="dashboard-controls">
          <label className="form-field form-field--inline" htmlFor="purpose-filter">
            <span>Filter by purpose</span>
            <select
              id="purpose-filter"
              value={purposeFilter}
              onChange={(event) => setPurposeFilter(event.target.value)}
            >
              <option value="ALL">All workloads</option>
              {PURPOSES.map((option) => (
                <option key={option} value={option}>{humanizeEnum(option)}</option>
              ))}
            </select>
          </label>
          <button type="button" className="btn btn--ghost" onClick={loadServices}>
            Refresh
          </button>
        </div>
      </header>

      {status === 'error' && (
        <div className="alert alert--error" role="alert">
          {error}
        </div>
      )}

      <section className="dashboard-section">
        {status === 'loading' && (
          <div className="empty-state">Loading your personalized catalog…</div>
        )}

        {status === 'success' && !filteredServices.length && (
          <div className="empty-state">No services match this filter yet. Try another workload type.</div>
        )}

        {status === 'success' && filteredServices.length > 0 && (
          <div className="services-grid">
            {filteredServices.map((service) => (
              <ServiceCard key={service.id} service={service} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default UserDashboard;
