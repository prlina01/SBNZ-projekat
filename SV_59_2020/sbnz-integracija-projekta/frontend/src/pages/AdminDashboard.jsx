import {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import ServiceForm from '../components/ServiceForm.jsx';
import {
  fetchServices,
  createService,
  updateService,
  deleteService,
  fetchPendingRentalRequests,
  approveRentalRequest,
  rejectRentalRequest,
} from '../api/services.js';
import { humanizeEnum, formatCurrency } from '../utils/formatters.js';
import { API_BASE_URL } from '../api/client.js';
import { useAuthContext } from '../context/AuthContext.jsx';

const AdminDashboard = () => {
  const { token } = useAuthContext();
  const [services, setServices] = useState([]);
  const [status, setStatus] = useState('idle');
  const [error, setError] = useState(null);
  const [formMode, setFormMode] = useState('create');
  const [selectedService, setSelectedService] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [notification, setNotification] = useState(null);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [pendingStatus, setPendingStatus] = useState('idle');
  const [pendingError, setPendingError] = useState(null);
  const [pendingAction, setPendingAction] = useState(null);
  const lastAlertByServer = useRef(new Map());

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

  const loadPendingRequests = useCallback(async () => {
    setPendingStatus('loading');
    setPendingError(null);
    try {
      const data = await fetchPendingRentalRequests();
      setPendingRequests(data);
      setPendingStatus('success');
    } catch (err) {
      const message = err?.response?.data?.message || 'Unable to load pending requests right now.';
      setPendingError(message);
      setPendingStatus('error');
    }
  }, []);

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  useEffect(() => {
    loadPendingRequests();
  }, [loadPendingRequests]);

  useEffect(() => {
    if (!notification) {
      return undefined;
    }

    const timeout = window.setTimeout(() => {
      setNotification(null);
    }, notification.dismissAfter ?? 4000);

    return () => window.clearTimeout(timeout);
  }, [notification]);

  useEffect(() => {
    if (!token) {
      return undefined;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000,
      debug: () => {},
    });

    let subscription = null;

    client.onConnect = () => {
      subscription = client.subscribe('/topic/admin-alerts', (frame) => {
        try {
          const payload = JSON.parse(frame.body);
          const typeValue = (payload?.type ?? '').toString().toLowerCase();
          const normalizedType = typeValue === 'positive' ? 'positive' : 'negative';
          const serverKey = payload?.serverId ?? payload?.serverName ?? 'unknown-server';

          const previousType = lastAlertByServer.current.get(serverKey);
          if (previousType === normalizedType) {
            return;
          }

          lastAlertByServer.current.set(serverKey, normalizedType);

          const decoratedMessage = [
            normalizedType === 'positive' ? '✅' : '⚠️',
            payload?.serverName ?? 'Unknown server',
            '•',
            payload?.content ?? payload?.originalMessage ?? 'New performance update available.',
            payload?.providerName ? `(${payload.providerName})` : '',
          ]
            .filter(Boolean)
            .join(' ');

          setNotification({
            type: normalizedType === 'positive' ? 'success' : 'danger',
            message: decoratedMessage,
            dismissAfter: 30000,
            banner: true,
          });
        } catch (err) {
          console.error('Failed to parse admin alert payload', err);
        }
      });
    };

    client.onStompError = (frame) => {
      console.error('WebSocket STOMP error', frame.body);
    };

    client.activate();

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
      client.deactivate();
    };
  }, [token]);

  const resetForm = useCallback(() => {
    setFormMode('create');
    setSelectedService(null);
  }, []);

  const renderAverageRating = useCallback((value) => {
    if (value === null || value === undefined) {
      return '—';
    }

    const rounded = Math.round(value * 10) / 10;
    return Number.isFinite(rounded) ? rounded.toFixed(1) : '—';
  }, []);

  const formatDateTime = useCallback((value) => {
    if (!value) {
      return '—';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '—';
    }
    return date.toLocaleString();
  }, []);

  const handleCreateOrUpdate = async (payload) => {
    setIsSubmitting(true);
    setNotification(null);

    try {
      if (formMode === 'edit' && selectedService) {
        await updateService(selectedService.id, payload);
        setNotification({ type: 'success', message: 'Service updated successfully.', dismissAfter: 4000 });
      } else {
        await createService(payload);
        setNotification({ type: 'success', message: 'Service created successfully.', dismissAfter: 4000 });
      }
      await loadServices();
      resetForm();
    } catch (err) {
      const message = err?.response?.data?.message || 'Saving failed. Please review the form and try again.';
      setNotification({ type: 'error', message, dismissAfter: 6000 });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEdit = useCallback((service) => {
    if ((service?.activeRentalCount ?? 0) > 0) {
      setNotification({
        type: 'warning',
        message: `"${service.name}" cannot be edited while it has active rentals.`,
        dismissAfter: 5000,
      });
      return;
    }

    setSelectedService(service);
    setFormMode('edit');
  }, []);

  const handleDelete = useCallback(async (service) => {
    if ((service?.activeRentalCount ?? 0) > 0) {
      setNotification({
        type: 'warning',
        message: `"${service.name}" cannot be deleted while it has active rentals.`,
        dismissAfter: 5000,
      });
      return;
    }

    const confirmed = window.confirm(`Delete service "${service.name}"? This action cannot be undone.`);
    if (!confirmed) {
      return;
    }

    setNotification(null);
    try {
      await deleteService(service.id);
      setNotification({ type: 'success', message: 'Service deleted.', dismissAfter: 4000 });
      await loadServices();
    } catch (err) {
      const message = err?.response?.data?.message || 'Delete failed. Please try again later.';
      setNotification({ type: 'error', message, dismissAfter: 6000 });
    }
  }, [loadServices]);

  const handleApproveRequest = useCallback(async (request) => {
    if (!request?.id) {
      return;
    }
    setPendingAction({ id: request.id, type: 'approve' });
    setNotification(null);
    try {
      await approveRentalRequest(request.id);
      const requester = request.username ? ` for ${request.username}` : '';
      setNotification({
        type: 'success',
        message: `Rental request${requester} approved.`,
        dismissAfter: 4000,
      });
      await Promise.all([loadPendingRequests(), loadServices()]);
    } catch (err) {
      const message = err?.response?.data?.message || 'Approval failed. Please try again later.';
      setNotification({ type: 'error', message, dismissAfter: 6000 });
    } finally {
      setPendingAction(null);
    }
  }, [loadPendingRequests, loadServices]);

  const handleRejectRequest = useCallback(async (request) => {
    if (!request?.id) {
      return;
    }
    const confirmed = window.confirm('Reject this rental request? This action cannot be undone.');
    if (!confirmed) {
      return;
    }

    setPendingAction({ id: request.id, type: 'reject' });
    setNotification(null);
    try {
      await rejectRentalRequest(request.id);
      const requester = request.username ? ` for ${request.username}` : '';
      setNotification({
        type: 'info',
        message: `Rental request${requester} rejected.`,
        dismissAfter: 4000,
      });
      await loadPendingRequests();
    } catch (err) {
      const message = err?.response?.data?.message || 'Rejecting failed. Please try again later.';
      setNotification({ type: 'error', message, dismissAfter: 6000 });
    } finally {
      setPendingAction(null);
    }
  }, [loadPendingRequests]);

  const tableBody = useMemo(() => {
    if (status === 'loading') {
      return (
        <tr>
          <td colSpan={8} className="table__status">Loading services…</td>
        </tr>
      );
    }

    if (status === 'error') {
      return (
        <tr>
          <td colSpan={8} className="table__status table__status--error">
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
          <td colSpan={8} className="table__status">No services yet. Create one using the form.</td>
        </tr>
      );
    }

    return services.map((service) => {
      const activeCount = service.activeRentalCount ?? 0;
      const locked = activeCount > 0;
      const lockMessage = locked ? 'Actions disabled while active rentals are present.' : undefined;

      return (
        <tr key={service.id}>
          <th scope="row">{service.name}</th>
          <td>{service.provider}</td>
          <td>{humanizeEnum(service.purpose)}</td>
          <td>{formatCurrency(service.pricePerHour)}</td>
          <td>{formatCurrency(service.pricePerMonth)}</td>
          <td>{activeCount}</td>
          <td>{renderAverageRating(service.averageRating)}</td>
          <td className="table__actions">
            <button
              type="button"
              className={`btn btn--ghost${locked ? ' btn--inactive' : ''}`}
              onClick={() => handleEdit(service)}
              title={lockMessage}
              aria-disabled={locked}
            >
              Edit
            </button>
            <button
              type="button"
              className={`btn btn--danger${locked ? ' btn--inactive' : ''}`}
              onClick={() => handleDelete(service)}
              title={lockMessage}
              aria-disabled={locked}
            >
              Delete
            </button>
          </td>
        </tr>
      );
    });
  }, [services, status, error, loadServices, handleEdit, handleDelete, renderAverageRating]);

  const pendingTableBody = useMemo(() => {
    if (pendingStatus === 'loading') {
      return (
        <tr>
          <td colSpan={6} className="table__status">Loading pending requests…</td>
        </tr>
      );
    }

    if (pendingStatus === 'error') {
      return (
        <tr>
          <td colSpan={6} className="table__status table__status--error">
            <p>{pendingError}</p>
            <button type="button" className="btn btn--ghost" onClick={loadPendingRequests}>
              Retry
            </button>
          </td>
        </tr>
      );
    }

    if (!pendingRequests.length) {
      return (
        <tr>
          <td colSpan={6} className="table__status">No pending requests right now.</td>
        </tr>
      );
    }

    return pendingRequests.map((request) => {
      const actionBusy = pendingAction?.id === request.id;
      const purposeLabel = request.purpose && request.purpose === request.purpose.toUpperCase()
        ? humanizeEnum(request.purpose)
        : (request.purpose || '—');
      return (
        <tr key={request.id}>
          <th scope="row">{request.username || 'Unknown user'}</th>
          <td>
            <div className="rental-table__service">
              <strong>{request.serviceName}</strong>
              <span>{request.providerName}</span>
            </div>
          </td>
          <td>{purposeLabel}</td>
          <td>{request.durationDays} day{request.durationDays === 1 ? '' : 's'}</td>
          <td>{formatDateTime(request.requestedAt)}</td>
          <td className="table__actions">
            <button
              type="button"
              className="btn btn--primary btn--inline"
              onClick={() => handleApproveRequest(request)}
              disabled={actionBusy}
            >
              {actionBusy && pendingAction?.type === 'approve' ? 'Approving…' : 'Approve'}
            </button>
            <button
              type="button"
              className="btn btn--danger btn--inline"
              onClick={() => handleRejectRequest(request)}
              disabled={actionBusy}
            >
              {actionBusy && pendingAction?.type === 'reject' ? 'Rejecting…' : 'Reject'}
            </button>
          </td>
        </tr>
      );
    });
  }, [pendingStatus, pendingRequests, pendingError, loadPendingRequests, pendingAction, formatDateTime, handleApproveRequest, handleRejectRequest]);

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
        <div
          className={`alert alert--${notification.type}${notification.banner ? ' alert--banner' : ''}`}
          role="status"
        >
          {notification.message}
        </div>
      )}

      <div className="admin-layout">
        <section className="admin-layout__primary">
          <div className="card">
            <header className="card__header">
              <h2>Pending rental approvals</h2>
              <span className="badge">{pendingRequests.length}</span>
            </header>
            <div className="card__body">
              <table className="data-table">
                <thead>
                  <tr>
                    <th scope="col">User</th>
                    <th scope="col">Service</th>
                    <th scope="col">Purpose</th>
                    <th scope="col">Duration</th>
                    <th scope="col">Requested at</th>
                    <th scope="col" className="table__actions">Actions</th>
                  </tr>
                </thead>
                <tbody>{pendingTableBody}</tbody>
              </table>
            </div>
          </div>

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
                    <th scope="col">Active rentals</th>
                    <th scope="col">Average rating</th>
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
