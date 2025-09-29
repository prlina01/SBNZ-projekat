import PropTypes from 'prop-types';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

const ProtectedRoute = ({ allowAdmin = false, allowUser = false }) => {
  const location = useLocation();
  const { isAuthenticated, isAdmin, isUser } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  const canAccess = (
    (!allowAdmin && !allowUser) ||
    (allowAdmin && isAdmin) ||
    (allowUser && isUser)
  );

  if (!canAccess) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

ProtectedRoute.propTypes = {
  allowAdmin: PropTypes.bool,
  allowUser: PropTypes.bool,
};

export default ProtectedRoute;
