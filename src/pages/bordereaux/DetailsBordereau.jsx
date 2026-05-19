import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const DetailsBordereau = () => {
  const { id } = useParams()
  const [bordereau, setBordereau] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/bordereau/detailsbordereau/${id}`)
      .then(res => setBordereau(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const b = bordereau || {}

  const cols = [
    { header: 'Référence', field: 'reference' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Offre', render: (r) => r.offre?.offreName || '-' },
    { header: 'Montant', render: (r) => formatCurrency(r.montant) },
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
  ]

  return (
    <section className="content">
      <PageHeader title="Détails bordereau"
        breadcrumb={[{ label: 'Bordereaux', path: '/bordereaux/Listebordereaux' }, { label: 'Détails' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Bordereau #{b.numeroBordereau || id}</h6>
            <div>
              <span className={`badge badge-${b.statut === 'Validé' ? 'success' : b.statut === 'Refusé' ? 'danger' : 'warning'} mr-2`}>
                {b.statut || '-'}
              </span>
              <Link to={`/bordereaux/editbordereaux/${id}`} className="btn btn-warning btn-sm">
                <i className="fas fa-edit mr-1"></i> Modifier
              </Link>
            </div>
          </div>
          <div className="card-body">
            <div className="row mb-4">
              <div className="col-md-6">
                <table className="table table-bordered">
                  <tbody>
                    <tr><th>N° Bordereau</th><td>{b.numeroBordereau || '-'}</td></tr>
                    <tr><th>Revendeur</th><td>{b.revendeur ? `${b.revendeur.firstName} ${b.revendeur.lastName}` : '-'}</td></tr>
                    <tr><th>Montant total</th><td>{formatCurrency(b.montant)}</td></tr>
                    <tr><th>Date</th><td>{formatDate(b.dateBordereau || b.dateCreation)}</td></tr>
                  </tbody>
                </table>
              </div>
            </div>

            {b.abonnements && (
              <>
                <h5>Abonnements dans ce bordereau</h5>
                <DataTable columns={cols} data={b.abonnements} emptyMessage="Aucun abonnement" />
              </>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default DetailsBordereau
