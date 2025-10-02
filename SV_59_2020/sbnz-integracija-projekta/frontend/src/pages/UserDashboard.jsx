import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchRentals, rateRental } from '../api/services.js';
import { humanizeEnum } from '../utils/formatters.js';

const ratingScale = [5, 4, 3, 2, 1];

const formatDate = (value) => {
  if (!value) {
    return '—';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return '—';
  }
  return date.toLocaleDateString();
};

const UserDashboard = () => {
  const [rentals, setRentals] = useState([]);
  const [status, setStatus] = useState('loading');
  const [error, setError] = useState(null);
  const [ratingDrafts, setRatingDrafts] = useState({});
  const [ratingBusy, setRatingBusy] = useState(null);
  const [ratingError, setRatingError] = useState(null);
  const [ratingFeedback, setRatingFeedback] = useState(null);

  const navigate = useNavigate();

  const rentalLabel = (rental) => `${rental.serviceName} (${rental.providerName})`;

  const loadRentals = useCallback(async () => {
    setStatus('loading');
    setError(null);
    setRatingFeedback(null);
    setRatingError(null);
    try {
      const data = await fetchRentals();
      setRentals(data);
      setStatus('success');
    } catch (err) {
      const message = err?.response?.data?.message || 'Unable to load your rentals right now.';
      setError(message);
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadRentals();
  }, [loadRentals]);

  const activeCount = useMemo(() => rentals.filter((rental) => rental.active).length, [rentals]);

  const handleDraftChange = (id, value) => {
    setRatingDrafts((prev) => ({
      ...prev,
      [id]: Number(value),
    }));
  };

  const handleRate = async (rental) => {
    const draft = Number(ratingDrafts[rental.id] ?? 5);
    setRatingBusy(rental.id);
    setRatingError(null);
    setRatingFeedback(null);
    try {
      const updated = await rateRental(rental.id, draft);
      setRentals((prev) => prev.map((item) => (item.id === rental.id ? updated : item)));
  setRatingFeedback(`Thanks! You rated ${rentalLabel(rental)} with ${draft}/5.`);
    } catch (err) {
      const message = err?.response?.data?.message || 'Unable to submit rating right now.';
      setRatingError(message);
    } finally {
      setRatingBusy(null);
    }
  };

  return (
    <div className="page dashboard-page">
      <header className="page__header">
        <div>
          <h1>Your rentals</h1>
          <p>Monitor active reservations, remaining time, and share feedback once you are done.</p>
        </div>
        <div className="dashboard-controls">
          <button type="button" className="btn btn--ghost" onClick={loadRentals}>
            Refresh
          </button>
          <button type="button" className="btn" onClick={() => navigate('/recommendations')}>
            Get Recommendations
          </button>
        </div>
      </header>

      {status === 'error' && (
        <div className="alert alert--error" role="alert">
          {error}
        </div>
      )}
      {ratingError && (
        <div className="alert alert--error" role="alert">
          {ratingError}
        </div>
      )}
      {ratingFeedback && (
        <div className="alert alert--success" role="status">
          {ratingFeedback}
        </div>
      )}

      <section className="dashboard-section">
        {status === 'loading' && (
          <div className="empty-state">Fetching your rentals…</div>
        )}

        {status === 'success' && !rentals.length && (
          <div className="empty-state">
            You don’t have any rentals yet. Explore recommendations to start your first reservation.
          </div>
        )}

        {status === 'success' && rentals.length > 0 && (
          <div className="card">
            <div className="card__header">
              <div>
                <h2>Rental overview</h2>
                <p className="card__subtitle">{activeCount} active rental{activeCount === 1 ? '' : 's'} out of {rentals.length}</p>
              </div>
            </div>
            <div className="card__body">
              <div className="table-wrapper">
                <table className="data-table rental-table">
                  <thead>
                    <tr>
                      <th scope="col">Service</th>
                      <th scope="col">Purpose</th>
                      <th scope="col">Start</th>
                      <th scope="col">Planned end</th>
                      <th scope="col">Remaining</th>
                      <th scope="col">Status</th>
                      <th scope="col">Rating</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rentals.map((rental) => {
                      const purposeLabel = rental.purpose && rental.purpose === rental.purpose.toUpperCase()
                        ? humanizeEnum(rental.purpose)
                        : (rental.purpose || '—');
                      const remainingLabel = rental.remainingDays > 0
                        ? `${rental.remainingDays} day${rental.remainingDays === 1 ? '' : 's'}`
                        : '0 days';
                      const statusLabel = rental.active ? 'Active' : rental.rating ? 'Completed' : 'Awaiting feedback';
                      const draft = ratingDrafts[rental.id] ?? 5;
                      return (
                        <tr key={rental.id} className={!rental.active ? 'rental-table__row--inactive' : undefined}>
                          <td>
                            <div className="rental-table__service">
                              <strong>{rental.serviceName}</strong>
                              <span>{rental.providerName}</span>
                            </div>
                          </td>
                          <td>{purposeLabel}</td>
                          <td>{formatDate(rental.startDate)}</td>
                          <td>{formatDate(rental.plannedEndDate)}</td>
                          <td>
                            <span className={rental.remainingDays > 0 ? 'badge badge--positive' : 'badge'}>{remainingLabel}</span>
                          </td>
                          <td>
                            <span className={rental.active ? 'status-badge status-badge--active' : 'status-badge'}>{statusLabel}</span>
                          </td>
                          <td>
                            {rental.rateable ? (
                              <div className="rental-table__rate">
                                <select
                                  value={draft}
                                  onChange={(event) => handleDraftChange(rental.id, event.target.value)}
                                  disabled={ratingBusy === rental.id}
                                >
                                  {ratingScale.map((value) => (
                                    <option key={value} value={value}>{value}</option>
                                  ))}
                                </select>
                                <button
                                  type="button"
                                  className="btn btn--primary btn--inline"
                                  onClick={() => handleRate(rental)}
                                  disabled={ratingBusy === rental.id}
                                >
                                  {ratingBusy === rental.id ? 'Saving…' : 'Submit'}
                                </button>
                              </div>
                            ) : (
                              <span>{rental.rating ? `${rental.rating}/5` : '—'}</span>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </section>
    </div>
  );
};

export default UserDashboard;
