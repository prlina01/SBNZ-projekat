import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth.js';

const LoginPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [form, setForm] = useState({ username: '', password: '' });
  const [status, setStatus] = useState('idle'); // idle | submitting | error
  const [error, setError] = useState(null);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (error) {
      setError(null);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!form.username || !form.password) {
      return;
    }

    setStatus('submitting');
    setError(null);

    try {
      const session = await login(form);
      const fallback = session.roles.includes('ADMIN') ? '/admin' : '/dashboard';
      const destination = location.state?.from?.pathname ?? fallback;
      navigate(destination, { replace: true });
    } catch (err) {
      const message = err.response?.data ?? 'Invalid credentials. Please try again.';
      setError(typeof message === 'string' ? message : 'Unable to sign in right now.');
      setStatus('error');
    } finally {
      setStatus((prev) => (prev === 'error' ? prev : 'idle'));
    }
  };

  const isSubmitting = status === 'submitting';

  return (
    <div className="auth-page">
      <div className="auth-card">
        <header className="auth-card__header">
          <h1>Welcome back</h1>
          <p>Sign in with your credentials to access your dashboard.</p>
        </header>
        <form className="auth-form" onSubmit={handleSubmit}>
          <label className="auth-form__field">
            <span>Username</span>
            <input
              type="text"
              name="username"
              autoComplete="username"
              value={form.username}
              onChange={handleChange}
              disabled={isSubmitting}
              required
            />
          </label>
          <label className="auth-form__field">
            <span>Password</span>
            <input
              type="password"
              name="password"
              autoComplete="current-password"
              value={form.password}
              onChange={handleChange}
              disabled={isSubmitting}
              required
            />
          </label>
          {error && <p className="auth-form__error" role="alert">{error}</p>}
          <button
            type="submit"
            className="btn btn--primary btn--full"
            disabled={isSubmitting || !form.username || !form.password}
          >
            {isSubmitting ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
        <p className="auth-card__footer">
          New to CloudCraft? <Link to="/register">Create an account</Link>.
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
