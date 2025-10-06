export const humanizeEnum = (value) => {
  if (!value) {
    return '';
  }
  const spaced = value.replace(/_/g, ' ').toLowerCase();
  return spaced.replace(/\b\w/g, (char) => char.toUpperCase());
};

export const formatCurrency = (amount, currency = 'USD') => {
  const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
  });
  return formatter.format(amount ?? 0);
};

export const formatUserStatus = (status) => {
  if (!status) {
    return 'Basic';
  }

  const map = {
    NONE: 'Basic',
    BRONZE: 'Bronze',
    SILVER: 'Silver',
    GOLD: 'Gold',
  };

  return map[status] ?? humanizeEnum(status);
};
