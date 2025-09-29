import { Outlet } from 'react-router-dom';
import Navbar from './Navbar.jsx';
import './AppLayout.css';

const AppLayout = () => (
  <div className="app-shell">
    <Navbar />
    <main className="app-main">
      <Outlet />
    </main>
    <footer className="app-footer">
      <span>© {new Date().getFullYear()} CloudCraft Platform. All rights reserved.</span>
    </footer>
  </div>
);

export default AppLayout;
