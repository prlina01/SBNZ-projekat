import { Link } from 'react-router-dom';

const NotFoundPage = () => (
  <div className="page notfound-page">
    <h1>404</h1>
    <p>The page you&apos;re looking for doesn&apos;t exist.</p>
    <Link className="btn btn--primary" to="/">Back to home</Link>
  </div>
);

export default NotFoundPage;
