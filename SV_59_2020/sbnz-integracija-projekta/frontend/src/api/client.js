import axios from 'axios';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const AUTH_STORAGE_KEY = 'sbnz_auth_session';

export const getStoredSession = () => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw);
  } catch (error) {
    console.warn('Failed to parse auth session from storage', error);
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }
};

export const storeSession = (session) => {
  if (!session) {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return;
  }
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
};

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: false,
});

api.interceptors.request.use((config) => {
  const session = getStoredSession();
  if (session?.token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${session.token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      storeSession(null);
    }
    return Promise.reject(error);
  },
);

export const authStorage = {
  key: AUTH_STORAGE_KEY,
  get: getStoredSession,
  set: storeSession,
  clear: () => storeSession(null),
};

export default api;
