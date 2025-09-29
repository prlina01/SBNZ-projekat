import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth.js';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [form, setForm] = useState({ username: '', password: '', confirmPassword: '' });
  const [status, setStatus] = useState('idle');
  const [error, setError] = useState(null);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (error) {
      setError(null);
    }
  };

  const passwordMismatch = form.password !== form.confirmPassword;
  const isDisabled =
    !form.username ||
    !form.password ||
    form.password.length < 6 ||
    passwordMismatch;

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (isDisabled) {
      return;
    }

    setStatus('submitting');
    setError(null);

    try {
      const session = await register({ username: form.username, password: form.password });
      const destination = session.roles.includes('ADMIN') ? '/admin' : '/dashboard';
      navigate(destination, { replace: true });
    } catch (err) {
      const message = err.response?.data ?? 'Unable to create account right now.';
      setError(typeof message === 'string' ? message : 'Unable to create account right now.');
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
          <h1>Create your account</h1>
          <p>Join CloudCraft to orchestrate AI-ready infrastructure with confidence.</p>
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
              autoComplete="new-password"
              value={form.password}
              onChange={handleChange}
              disabled={isSubmitting}
              required
            />
          </label>
          <label className="auth-form__field">
            <span>Confirm password</span>
            <input
              type="password"
              name="confirmPassword"
              autoComplete="new-password"
              value={form.confirmPassword}
              onChange={handleChange}
              disabled={isSubmitting}
              required
            />
          </label>
          {passwordMismatch && form.confirmPassword && (
            <p className="auth-form__error" role="alert">Passwords do not match.</p>
          )}
          {error && <p className="auth-form__error" role="alert">{error}</p>}
          <button type="submit" className="btn btn--primary btn--full" disabled={isSubmitting || isDisabled}>
            {isSubmitting ? 'Creating account…' : 'Create account'}
          </button>
        </form>
        <p className="auth-card__footer">
          Already have an account? <Link to="/login">Sign in</Link>.
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
