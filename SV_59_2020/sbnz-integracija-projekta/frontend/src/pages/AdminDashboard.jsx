import { useCallback, useEffect, useMemo, useState } from 'react';
import ServiceForm from '../components/ServiceForm.jsx';
import { fetchServices, createService, updateService, deleteService } from '../api/services.js';
import { humanizeEnum, formatCurrency } from '../utils/formatters.js';

const AdminDashboard = () => {
  const [services, setServices] = useState([]);
  const [status, setStatus] = useState('idle');
  const [error, setError] = useState(null);
  const [formMode, setFormMode] = useState('create');
  const [selectedService, setSelectedService] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [notification, setNotification] = useState(null);

  const loadServices = useCallback(async () => {
    setStatus('loading');
    setError(null);
    try {
      const data = await fetchServices();
      setServices(data);
      setStatus('success');
    } catch (err) {
      const message = err?.response?.data?.message || 'Unable to load services right now.';
      setError(message);
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  useEffect(() => {
    if (!notification) {
      return undefined;
    }

    const timeout = window.setTimeout(() => {
      setNotification(null);
    }, 4000);

    return () => window.clearTimeout(timeout);
  }, [notification]);

  const resetForm = useCallback(() => {
    setFormMode('create');
    setSelectedService(null);
  }, []);

  const handleCreateOrUpdate = async (payload) => {
    setIsSubmitting(true);
    setNotification(null);

    try {
      if (formMode === 'edit' && selectedService) {
        await updateService(selectedService.id, payload);
        setNotification({ type: 'success', message: 'Service updated successfully.' });
      } else {
        await createService(payload);
        setNotification({ type: 'success', message: 'Service created successfully.' });
      }
      await loadServices();
      resetForm();
    } catch (err) {
      const message = err?.response?.data?.message || 'Saving failed. Please review the form and try again.';
      setNotification({ type: 'error', message });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEdit = useCallback((service) => {
    setSelectedService(service);
    setFormMode('edit');
  }, []);

  const handleDelete = useCallback(async (service) => {
    const confirmed = window.confirm(`Delete service "${service.name}"? This action cannot be undone.`);
    if (!confirmed) {
      return;
    }

    setNotification(null);
    try {
      await deleteService(service.id);
      setNotification({ type: 'success', message: 'Service deleted.' });
      await loadServices();
    } catch (err) {
      const message = err?.response?.data?.message || 'Delete failed. Please try again later.';
      setNotification({ type: 'error', message });
    }
  }, [loadServices]);

  const tableBody = useMemo(() => {
    if (status === 'loading') {
      return (
        <tr>
          <td colSpan={6} className="table__status">Loading services…</td>
        </tr>
      );
    }

    if (status === 'error') {
      return (
        <tr>
          <td colSpan={6} className="table__status table__status--error">
            <p>{error}</p>
            <button type="button" className="btn btn--ghost" onClick={loadServices}>
              Retry
            </button>
          </td>
        </tr>
      );
    }

    if (!services.length) {
      return (
        <tr>
          <td colSpan={6} className="table__status">No services yet. Create one using the form.</td>
        </tr>
      );
    }

    return services.map((service) => (
      <tr key={service.id}>
        <th scope="row">{service.name}</th>
        <td>{service.provider}</td>
        <td>{humanizeEnum(service.purpose)}</td>
        <td>{formatCurrency(service.pricePerHour)}</td>
        <td>{formatCurrency(service.pricePerMonth)}</td>
        <td className="table__actions">
          <button
            type="button"
            className="btn btn--ghost"
            onClick={() => handleEdit(service)}
          >
            Edit
          </button>
          <button
            type="button"
            className="btn btn--danger"
            onClick={() => handleDelete(service)}
          >
            Delete
          </button>
        </td>
      </tr>
    ));
  }, [services, status, error, loadServices, handleEdit, handleDelete]);

  return (
    <div className="page admin-page">
      <header className="page__header">
        <div>
          <h1>Service catalog administration</h1>
          <p>Manage offerings, enforce guardrails, and keep recommendations fresh.</p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={loadServices}>
          Refresh list
        </button>
      </header>

      {notification && (
        <div className={`alert alert--${notification.type}`} role="status">
          {notification.message}
        </div>
      )}

      <div className="admin-layout">
        <section className="admin-layout__primary">
          <div className="card">
            <header className="card__header">
              <h2>Catalog overview</h2>
              <span className="badge">{services.length}</span>
            </header>
            <div className="card__body">
              <table className="data-table">
                <thead>
                  <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Provider</th>
                    <th scope="col">Purpose</th>
                    <th scope="col">Hourly price</th>
                    <th scope="col">Monthly price</th>
                    <th scope="col" className="table__actions">Actions</th>
                  </tr>
                </thead>
                <tbody>{tableBody}</tbody>
              </table>
            </div>
          </div>
        </section>

        <aside className="admin-layout__form">
          <div className="card">
            <div className="card__body">
              <ServiceForm
                mode={formMode}
                initialValues={selectedService}
                onSubmit={handleCreateOrUpdate}
                onCancel={resetForm}
                isSubmitting={isSubmitting}
              />
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
};

export default AdminDashboard;
