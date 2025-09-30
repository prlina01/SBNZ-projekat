import { Navigate, Route, Routes } from 'react-router-dom';
import AppLayout from './components/AppLayout.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import useAuth from './hooks/useAuth.js';
import LandingPage from './pages/LandingPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import UserDashboard from './pages/UserDashboard.jsx';
import AdminDashboard from './pages/AdminDashboard.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';
import './App.css';

import RecommendationsPage from './pages/RecommendationsPage.jsx';

const App = () => {
  const { isAuthenticated, isAdmin } = useAuth();

  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route
          index
          element={
            isAuthenticated ? (
              <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />
            ) : (
              <LandingPage />
            )
          }
        />
        <Route
          path="/login"
          element={
            isAuthenticated ? (
              <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />
            ) : (
              <LoginPage />
            )
          }
        />
        <Route
          path="/register"
          element={
            isAuthenticated ? (
              <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />
            ) : (
              <RegisterPage />
            )
          }
        />

        <Route element={<ProtectedRoute allowUser /> }>
          <Route path="/dashboard" element={<UserDashboard />} />
          <Route path="/recommendations" element={<RecommendationsPage />} />
        </Route>
        <Route element={<ProtectedRoute allowAdmin /> }>
          <Route path="/admin" element={<AdminDashboard />} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
};

export default App;
