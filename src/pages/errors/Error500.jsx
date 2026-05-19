import React from 'react'
import { Link } from 'react-router-dom'

const Error500 = () => {
  return (
    <div className="hold-transition" style={{ background: '#fff' }}>
      <section className="content" style={{ padding: '80px 0' }}>
        <div className="container-fluid text-center">
          <div className="error-page">
            <h2 className="headline" style={{ color: '#dc3545', fontSize: '10rem', fontWeight: 700 }}>500</h2>
            <div className="error-content d-inline-block">
              <h3>
                <i className="fas fa-times-circle text-danger mr-2"></i>
                Erreur serveur !
              </h3>
              <p className="text-muted mb-4">
                Une erreur interne s&apos;est produite. Veuillez réessayer plus tard.
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

export default Error500
