import PropTypes from 'prop-types';
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import * as authApi from '../api/auth';
import { authStorage } from '../api/client';

const AuthContext = createContext(null);

const decodeJwtPayload = (token) => {
  if (!token) {
    return null;
  }
  try {
    const payload = token.split('.')[1];
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const decoded = atob(normalized);
    return JSON.parse(decoded);
  } catch (error) {
    console.warn('Failed to decode JWT payload', error);
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [session, setSession] = useState(() => authStorage.get());

  useEffect(() => {
    if (session) {
      authStorage.set(session);
    } else {
      authStorage.clear();
    }
  }, [session]);

  const buildSession = useCallback((token, roles, fallbackUsername) => {
    const payload = decodeJwtPayload(token);
    const username = payload?.sub ?? fallbackUsername ?? null;
    return { token, roles: Array.from(roles ?? []), username };
  }, []);

  const login = useCallback(
    async (credentials) => {
      const data = await authApi.login(credentials);
      const nextSession = buildSession(data.token, data.roles, credentials.username);
      setSession(nextSession);
      return nextSession;
    },
    [buildSession],
  );

  const register = useCallback(
    async (payload) => {
      const data = await authApi.register(payload);
      const nextSession = buildSession(data.token, data.roles, payload.username);
      setSession(nextSession);
      return nextSession;
    },
    [buildSession],
  );

  const logout = useCallback(() => {
    setSession(null);
  }, []);

  const value = useMemo(
    () => ({
      token: session?.token ?? null,
      roles: session?.roles ?? [],
      username: session?.username ?? null,
      isAuthenticated: Boolean(session?.token),
      isAdmin: (session?.roles ?? []).includes('ADMIN'),
      isUser: (session?.roles ?? []).includes('USER'),
      login,
      register,
      logout,
    }),
    [session, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export const useAuthContext = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuthContext must be used within an AuthProvider');
  }
  return context;
};
