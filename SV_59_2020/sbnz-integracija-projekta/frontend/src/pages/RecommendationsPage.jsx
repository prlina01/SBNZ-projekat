import { useMemo, useState } from 'react';
import RecommendationForm from '../components/RecommendationForm.jsx';
import ServiceCard from '../components/ServiceCard.jsx';
import { fetchRecommendations, rentServiceOffering } from '../api/services.js';
import { humanizeEnum } from '../utils/formatters.js';
import useAuth from '../hooks/useAuth';

const RecommendationsPage = () => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [lastQuery, setLastQuery] = useState(null);
  const [rentingId, setRentingId] = useState(null);
  const [rentFeedback, setRentFeedback] = useState(null);
  const [rentError, setRentError] = useState(null);
  const { refreshProfile } = useAuth();

  const handleSubmit = async (payload, rawFilters) => {
    setLoading(true);
    setError(null);
    setRentFeedback(null);
    setRentError(null);
    setResults([]);
    setHasSearched(true);
    try {
      const data = await fetchRecommendations(payload);
      setResults(data);
      const durationDays = Number(rawFilters?.rentalDuration ?? payload?.rentalDuration ?? 30) || 30;
      const purposeRaw = rawFilters?.purpose ?? payload?.purpose;
      const purpose = purposeRaw && purposeRaw !== 'ANY' ? purposeRaw : 'GENERAL_WORKLOAD';
      setLastQuery({
        durationDays,
        purpose,
      });
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to fetch recommendations.');
    } finally {
      setLoading(false);
    }
  };

  const handleRent = async (service) => {
    if (!lastQuery) {
      setRentError('Run a recommendation search first so we know how long to reserve the server.');
      return;
    }

    setRentError(null);
    setRentFeedback(null);
    setRentingId(service.id);

    const durationDays = Number(lastQuery.durationDays) || 30;
    const purposeLabel = lastQuery.purpose && lastQuery.purpose !== 'GENERAL_WORKLOAD'
      ? lastQuery.purpose
      : `Rental for ${service.name}`;

    try {
      await rentServiceOffering({
        serviceOfferingId: service.id,
        purpose: purposeLabel,
        durationDays,
      });
      await refreshProfile().catch((error) => {
        console.warn('Failed to refresh profile after rental', error);
      });
      setRentFeedback(`Rental confirmed – ${service.name} is yours for ${durationDays} day${durationDays === 1 ? '' : 's'}.`);
    } catch (err) {
      setRentError(err?.response?.data?.message || 'Unable to rent this server right now.');
    } finally {
      setRentingId(null);
    }
  };

  const rentContextLabel = useMemo(() => {
    if (!lastQuery) {
      return null;
    }
    const prettyPurpose = lastQuery.purpose && lastQuery.purpose !== 'GENERAL_WORKLOAD'
      ? humanizeEnum(lastQuery.purpose)
      : 'General workload';
    return `Rentals will use purpose “${prettyPurpose}” for ${lastQuery.durationDays} day${lastQuery.durationDays === 1 ? '' : 's'}.`;
  }, [lastQuery]);

  return (
    <div className="page recommendations-page">
      <header className="recommendations-hero">
        <div>
          <h1>Find the best cloud match</h1>
          <p>
            Describe your workload, reliability targets, and compliance needs. We’ll blend expert
            knowledge with Drools rules to surface offers that fit like a glove.
          </p>
        </div>
        <div className="recommendations-hero__stats">
          <span><strong>30+</strong> curated providers</span>
          <span><strong>50ms</strong> average scoring time</span>
          <span><strong>Rule-driven</strong> personalization</span>
        </div>
      </header>

      <RecommendationForm onSubmit={handleSubmit} loading={loading} />

      {error && <div className="alert alert--error" role="alert">{error}</div>}
      {rentError && <div className="alert alert--error" role="alert">{rentError}</div>}
      {rentFeedback && <div className="alert alert--success" role="status">{rentFeedback}</div>}
      {rentContextLabel && <div className="alert alert--info" role="status">{rentContextLabel}</div>}

      <section className="recommendations-results">
        {loading && <div className="empty-state">Searching for recommendations…</div>}

        {!loading && results.length > 0 && (
          <div className="recommendations-list">
            {results.map((service) => (
              <ServiceCard
                key={service.id}
                service={service}
                onRent={handleRent}
                renting={rentingId === service.id}
              />
            ))}
          </div>
        )}

        {!loading && hasSearched && results.length === 0 && (
          <div className="empty-state">No service found for your query.</div>
        )}
      </section>
    </div>
  );
};

export default RecommendationsPage;
