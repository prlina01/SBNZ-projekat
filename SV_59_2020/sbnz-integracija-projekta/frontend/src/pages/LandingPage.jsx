import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { fetchFeaturedServices } from '../api/services.js';
import ServiceCard from '../components/ServiceCard.jsx';

const LandingPage = () => {
  const [services, setServices] = useState([]);
  const [status, setStatus] = useState('idle'); // idle | loading | success | error

  useEffect(() => {
    let isSubscribed = true;

    const loadFeatured = async () => {
      setStatus('loading');
      try {
        const data = await fetchFeaturedServices(2);
        if (isSubscribed) {
          setServices(data);
          setStatus('success');
        }
      } catch (error) {
        console.error('Failed to load featured services', error);
        if (isSubscribed) {
          setStatus('error');
        }
      }
    };

    loadFeatured();

    return () => {
      isSubscribed = false;
    };
  }, []);

  return (
    <div className="page landing-page">
      <section className="hero">
        <div className="hero__content">
          <h1>Harness AI-ready cloud infrastructure without the guesswork.</h1>
          <p>
            CloudCraft analyzes your workloads, provisions high-performance compute, and continuously tunes deployments
            using expert Drools rulesets so you can focus on delivering value.
          </p>
          <div className="hero__actions">
            <Link className="btn btn--primary" to="/register">Get started</Link>
            <a className="btn btn--ghost" href="#explore">Explore services</a>
          </div>
        </div>
        <div className="hero__illustration" aria-hidden>
          <div className="hero__orb" />
          <div className="hero__card">
            <span className="hero__card-title">Real-time usage guard</span>
            <p className="hero__card-body">Drools-driven insights keep your infrastructure healthy and cost-optimized.</p>
          </div>
        </div>
      </section>

      <section id="explore" className="section section--muted">
        <header className="section__header">
          <h2>Featured service offerings</h2>
          <p>Preview a few deployments curated by our expert system.</p>
        </header>
        <div className="services-grid">
          {status === 'loading' && (
            <>
              <div className="service-card service-card--placeholder" />
              <div className="service-card service-card--placeholder" />
            </>
          )}
          {status === 'error' && (
            <div className="service-card service-card--error">
              <strong>Unable to load services right now.</strong>
              <span>Please try again shortly.</span>
            </div>
          )}
          {status === 'success' && services.length === 0 && (
            <div className="service-card service-card--empty">
              <strong>No service offerings yet</strong>
              <span>Administrators can add offerings from the admin dashboard.</span>
            </div>
          )}
          {status === 'success' && services.map((service) => (
            <ServiceCard key={service.id} service={service} />
          ))}
        </div>
      </section>

      <section className="section">
        <header className="section__header">
          <h2>Why teams pick CloudCraft</h2>
        </header>
        <div className="feature-grid">
          <article className="feature-card">
            <h3>Opinionated best practices</h3>
            <p>Encode infrastructure policy in Drools once and roll it out everywhere.</p>
          </article>
          <article className="feature-card">
            <h3>Transparent governance</h3>
            <p>Built-in guardrails ensure administrators can safely evolve service offerings.</p>
          </article>
          <article className="feature-card">
            <h3>AI-ready by design</h3>
            <p>GPU-tuned topologies, encrypted storage, and high-availability baked-in.</p>
          </article>
        </div>
      </section>
    </div>
  );
};

export default LandingPage;
