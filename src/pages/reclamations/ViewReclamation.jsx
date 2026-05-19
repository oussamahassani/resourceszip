import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatDateTime } from '../../utils/helpers'

const ViewReclamation = () => {
  const { id } = useParams()
  const [reclamation, setReclamation] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/reclamation/view/${id}`)
      .then(res => setReclamation(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const r = reclamation || {}

  return (
    <section className="content">
      <PageHeader title="Détails réclamation"
        breadcrumb={[{ label: 'Réclamations', path: '/reclamation/allreclamations' }, { label: 'Détails' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Réclamation #{r.numeroReclamation || id}</h6>
            <div>
              <span className={`badge badge-${r.statut === 'Résolu' ? 'success' : r.statut === 'En cours' ? 'warning' : 'secondary'} mr-2`}>
                {r.statut || '-'}
              </span>
              <Link to={`/reclamation/edit/${id}`} className="btn btn-warning btn-sm">
                <i className="fas fa-edit mr-1"></i> Modifier
              </Link>
            </div>
          </div>
          <div className="card-body">
            <div className="row">
              <div className="col-md-6">
                <table className="table table-bordered table-sm">
                  <tbody>
                    <tr><th>Client</th><td>{r.client?.firstName} {r.client?.lastName}</td></tr>
                    <tr><th>CIN</th><td>{r.client?.cin || '-'}</td></tr>
                    <tr><th>Téléphone</th><td>{r.client?.telephone || '-'}</td></tr>
                    <tr><th>Référence</th><td>{r.reference || '-'}</td></tr>
                    <tr><th>Motif</th><td>{r.motif?.motifName || '-'}</td></tr>
                    <tr><th>Type visite</th><td>{r.typeVisite?.typeVisiteName || '-'}</td></tr>
                  </tbody>
                </table>
              </div>
              <div className="col-md-6">
                <table className="table table-bordered table-sm">
                  <tbody>
                    <tr><th>Date création</th><td>{formatDateTime(r.dateCreation)}</td></tr>
                    <tr><th>Date intervention</th><td>{formatDate(r.dateIntervention)}</td></tr>
                    <tr><th>Assigné à</th><td>{r.assignedTo ? `${r.assignedTo.firstName} ${r.assignedTo.lastName}` : '-'}</td></tr>
                    <tr><th>Créé par</th><td>{r.createdBy || '-'}</td></tr>
                  </tbody>
                </table>
              </div>
              <div className="col-12 mt-3">
                <h6 className="font-weight-bold">Description</h6>
                <div className="p-3 bg-light rounded">{r.description || '-'}</div>
              </div>
              {r.resolution && (
                <div className="col-12 mt-3">
                  <h6 className="font-weight-bold">Résolution</h6>
                  <div className="p-3 bg-light rounded">{r.resolution}</div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default ViewReclamation
