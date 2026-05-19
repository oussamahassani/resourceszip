import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import Loader from '../../components/common/Loader'
import usersService from '../../services/usersService'
import { formatDate, formatCurrency } from '../../utils/helpers'

const DetailRevendeur = () => {
  const { id } = useParams()
  const [revendeur, setRevendeur] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    usersService.getRevendeurById(id)
      .then(setRevendeur)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const r = revendeur || {}

  return (
    <section className="content">
      <PageHeader
        title="Détails du revendeur"
        breadcrumb={[{ label: 'Revendeurs', path: '/RevendeurUser/revendeurliste/1' }, { label: 'Détails' }]}
      />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>
              {r.firstName} {r.lastName}
            </h6>
            <Link to={`/RevendeurUser/editrevendeur/${id}`} className="btn btn-warning btn-sm">
              <i className="fas fa-edit mr-1"></i> Modifier
            </Link>
          </div>
          <div className="card-body">
            <div className="row">
              <div className="col-md-6">
                <table className="table table-bordered">
                  <tbody>
                    <tr><th>Code</th><td>{r.codeUser}</td></tr>
                    <tr><th>Nom complet</th><td>{r.firstName} {r.lastName}</td></tr>
                    <tr><th>Email</th><td>{r.email}</td></tr>
                    <tr><th>Téléphone</th><td>{r.phone}</td></tr>
                    <tr><th>Zone</th><td>{r.zone?.zoneName || '-'}</td></tr>
                    <tr><th>Statut</th><td>
                      <span className={`badge badge-${r.enabled ? 'success' : 'danger'}`}>
                        {r.enabled ? 'Actif' : 'Inactif'}
                      </span>
                    </td></tr>
                    <tr><th>Date création</th><td>{formatDate(r.dateCreation)}</td></tr>
                  </tbody>
                </table>
              </div>
              <div className="col-md-6">
                <div className="card border-left-primary shadow py-2 mb-3">
                  <div className="card-body py-2">
                    <div className="row no-gutters align-items-center">
                      <div className="col">
                        <div className="text-xs font-weight-bold text-primary text-uppercase mb-1">Plafond autorisé</div>
                        <div className="h5 mb-0 font-weight-bold">{formatCurrency(r.plafond)}</div>
                      </div>
                      <div className="col-auto">
                        <i className="fas fa-money-bill-wave fa-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="card border-left-warning shadow py-2">
                  <div className="card-body py-2">
                    <div className="row no-gutters align-items-center">
                      <div className="col">
                        <div className="text-xs font-weight-bold text-warning text-uppercase mb-1">Montant non versé</div>
                        <div className="h5 mb-0 font-weight-bold">{formatCurrency(r.montantNonVerse)}</div>
                      </div>
                      <div className="col-auto">
                        <i className="fas fa-exclamation-triangle fa-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default DetailRevendeur
