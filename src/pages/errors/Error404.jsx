import React from 'react'
import { Link } from 'react-router-dom'

const Error404 = () => {
  return (
    <div className="hold-transition" style={{ background: '#fff' }}>
      <section className="content" style={{ padding: '80px 0' }}>
        <div className="container-fluid text-center">
          <div className="error-page">
            <h2 className="headline" style={{ color: '#590E9C', fontSize: '10rem', fontWeight: 700 }}>404</h2>
            <div className="error-content d-inline-block">
              <h3>
                <i className="fas fa-exclamation-triangle text-warning mr-2"></i>
                Page introuvable !
              </h3>
              <p className="text-muted mb-4">
                La page que vous recherchez n&apos;existe pas ou a été déplacée.
              </p>
              <Link to="/" className="btn btn-primary">
                <i className="fas fa-home mr-2"></i> Retour à l&apos;accueil
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

export default Error404
