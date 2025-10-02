import api from './client';

export const fetchServices = async (params = {}) => {
  const { data } = await api.get('/api/services', { params });
  return data;
};

export const fetchRecommendations = async (payload) => {
  const { data } = await api.post('/api/services/recommendations', payload);
  return data;
};

export const fetchServiceById = async (id) => {
  const { data } = await api.get(`/api/services/${id}`);
  return data;
};

export const fetchFeaturedServices = async (count = 2) => {
  const { data } = await api.get('/api/services/featured', {
    params: { count },
  });
  return data;
};

export const createService = async (payload) => {
  const { data } = await api.post('/api/admin/services', payload);
  return data;
};

export const updateService = async (id, payload) => {
  const { data } = await api.put(`/api/admin/services/${id}`, payload);
  return data;
};

export const deleteService = async (id) => {
  await api.delete(`/api/admin/services/${id}`);
};

export const rentServiceOffering = async (payload) => {
  const { data } = await api.post('/api/rentals', payload);
  return data;
};

export const fetchRentals = async () => {
  const { data } = await api.get('/api/rentals');
  return data;
};

export const rateRental = async (rentalId, score) => {
  const { data } = await api.post(`/api/rentals/${rentalId}/rating`, { score });
  return data;
};
