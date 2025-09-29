import { Link, NavLink, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const { isAuthenticated, isAdmin, logout, username } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="navbar">
      <div className="navbar__brand">
        <Link to="/" className="navbar__logo">CloudCraft</Link>
      </div>
      <nav className="navbar__nav">
        <NavLink to="/" className={({ isActive }) => `navbar__link${isActive ? ' active' : ''}`} end>
          Home
        </NavLink>
        {isAuthenticated && (
          <NavLink
            to={isAdmin ? '/admin' : '/dashboard'}
            className={({ isActive }) => `navbar__link${isActive ? ' active' : ''}`}
          >
            Dashboard
          </NavLink>
        )}
        {!isAuthenticated && (
          <>
            <NavLink to="/login" className={({ isActive }) => `navbar__link${isActive ? ' active' : ''}`}>
              Login
            </NavLink>
            <NavLink
              to="/register"
              className={({ isActive }) => `navbar__link navbar__cta${isActive ? ' active' : ''}`}
            >
              Sign up
            </NavLink>
          </>
        )}
        {isAuthenticated && (
          <button type="button" className="navbar__logout" onClick={handleLogout}>
            Logout {username ? `(${username})` : ''}
          </button>
        )}
      </nav>
    </header>
  );
};

export default Navbar;
