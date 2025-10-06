import api from './client';

export const login = async (credentials) => {
  const { data } = await api.post('/api/auth/login', credentials);
  return data;
};

export const register = async (payload) => {
  const { data } = await api.post('/api/auth/register', payload);
  return data;
};

export const fetchCurrentUser = async () => {
  const { data } = await api.get('/api/users/me');
  return data;
};
