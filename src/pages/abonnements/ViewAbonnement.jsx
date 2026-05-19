import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import Loader from '../../components/common/Loader'
import abonnementsService from '../../services/abonnementsService'
import { formatDate, formatDateTime } from '../../utils/helpers'

const ViewAbonnement = () => {
  const { id } = useParams()
  const [abonnement, setAbonnement] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    abonnementsService.getById(id)
      .then(setAbonnement)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const a = abonnement || {}

  return (
    <section className="content">
      <PageHeader
        title="Détails abonnement"
        breadcrumb={[{ label: 'Abonnements', path: '/demandeabonnement/alldemandesabonnement/1/20' }, { label: 'Détails' }]}
      />
      <div className="container-fluid">
        <div className="row">
          <div className="col-md-8">
            <div className="card shadow mb-4">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>
                  Demande #{a.reference || id}
                </h6>
                <div>
                  <span className={`badge badge-${a.statut === 'Validé' ? 'success' : a.statut === 'Refusé' ? 'danger' : a.statut === 'En cours' ? 'warning' : 'secondary'} mr-2`}>
                    {a.statut || '-'}
                  </span>
                  <Link to={`/demandeabonnement/editdemandeabonnement/${id}`} className="btn btn-warning btn-sm">
                    <i className="fas fa-edit mr-1"></i> Modifier
                  </Link>
                </div>
              </div>
              <div className="card-body">
                <div className="row">
                  <div className="col-md-6">
                    <h6 className="font-weight-bold mb-2">Client</h6>
                    <table className="table table-sm">
                      <tbody>
                        <tr><th>Nom</th><td>{a.client?.firstName} {a.client?.lastName}</td></tr>
                        <tr><th>CIN</th><td>{a.client?.cin || '-'}</td></tr>
                        <tr><th>Téléphone</th><td>{a.client?.telephone || '-'}</td></tr>
                        <tr><th>Email</th><td>{a.client?.email || '-'}</td></tr>
                        <tr><th>Adresse</th><td>{a.client?.adresse || '-'}</td></tr>
                        <tr><th>Gouvernorat</th><td>{a.client?.gouvernorat?.gouvernoratName || '-'}</td></tr>
                      </tbody>
                    </table>
                  </div>
                  <div className="col-md-6">
                    <h6 className="font-weight-bold mb-2">Abonnement</h6>
                    <table className="table table-sm">
                      <tbody>
                        <tr><th>Référence</th><td>{a.reference || '-'}</td></tr>
                        <tr><th>Offre</th><td>{a.offre?.offreName || '-'}</td></tr>
                        <tr><th>Pack</th><td>{a.pack?.packName || '-'}</td></tr>
                        <tr><th>Zone</th><td>{a.zone?.zoneName || '-'}</td></tr>
                        <tr><th>Type paiement</th><td>{a.typePaiement?.typePaiementName || '-'}</td></tr>
                        <tr><th>Revendeur</th><td>{a.revendeur ? `${a.revendeur.firstName} ${a.revendeur.lastName}` : '-'}</td></tr>
                      </tbody>
                    </table>
                  </div>
                </div>
                <hr />
                <div className="row">
                  <div className="col-md-6">
                    <h6 className="font-weight-bold mb-2">Technique</h6>
                    <table className="table table-sm">
                      <tbody>
                        <tr><th>Login Modem</th><td>{a.loginModem || '-'}</td></tr>
                        <tr><th>N° Série Modem</th><td>{a.serialModem || '-'}</td></tr>
                        <tr><th>Date installation</th><td>{formatDate(a.dateInstallation)}</td></tr>
                      </tbody>
                    </table>
                  </div>
                  <div className="col-md-6">
                    <h6 className="font-weight-bold mb-2">Suivi</h6>
                    <table className="table table-sm">
                      <tbody>
                        <tr><th>Date création</th><td>{formatDateTime(a.dateCreation)}</td></tr>
                        <tr><th>Créé par</th><td>{a.createdBy || '-'}</td></tr>
                        <tr><th>Date modification</th><td>{formatDateTime(a.dateModification)}</td></tr>
                      </tbody>
                    </table>
                  </div>
                </div>
                {a.commentaire && (
                  <div className="mt-3">
                    <h6 className="font-weight-bold">Commentaire</h6>
                    <p className="text-muted">{a.commentaire}</p>
                  </div>
                )}
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="card shadow">
              <div className="card-header">
                <h6 className="m-0 font-weight-bold">Actions</h6>
              </div>
              <div className="card-body">
                <div className="d-grid gap-2">
                  <Link to={`/demandeabonnement/editdemandeabonnement/${id}`} className="btn btn-warning btn-block mb-2">
                    <i className="fas fa-edit mr-2"></i> Modifier
                  </Link>
                  <button onClick={() => window.print()} className="btn btn-secondary btn-block mb-2">
                    <i className="fas fa-print mr-2"></i> Imprimer
                  </button>
                  <Link to="/demandeabonnement/alldemandesabonnement/1/20" className="btn btn-light btn-block">
                    <i className="fas fa-arrow-left mr-2"></i> Retour à la liste
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default ViewAbonnement
