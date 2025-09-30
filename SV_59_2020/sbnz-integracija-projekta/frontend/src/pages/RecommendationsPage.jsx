import { useState } from 'react';
import RecommendationForm from '../components/RecommendationForm.jsx';
import ServiceCard from '../components/ServiceCard.jsx';
import { fetchRecommendations } from '../api/services.js';

const RecommendationsPage = () => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  const handleSubmit = async (filters) => {
    setLoading(true);
    setError(null);
    setResults([]);
    setHasSearched(true);
    try {
      const data = await fetchRecommendations(filters);
      setResults(data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to fetch recommendations.');
    } finally {
      setLoading(false);
    }
  };

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

      <section className="recommendations-results">
        {loading && <div className="empty-state">Searching for recommendations…</div>}

        {!loading && results.length > 0 && (
          <div className="services-grid">
            {results.map((service) => (
              <ServiceCard key={service.id} service={service} />
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
