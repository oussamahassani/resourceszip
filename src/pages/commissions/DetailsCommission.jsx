import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const DetailsCommission = () => {
  const { id } = useParams()
  const [commission, setCommission] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/commission/detailsCommission/${id}`)
      .then(res => setCommission(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const c = commission || {}

  const columns = [
    { header: 'Référence', render: (r) => r.reference || r.demandeId || '-' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Offre', render: (r) => r.offre?.offreName || '-' },
    { header: 'Commission (TND)', render: (r) => formatCurrency(r.montantCommission) },
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
  ]

  return (
    <section className="content">
      <PageHeader title="Détails commission" breadcrumb={[{ label: 'Commissions', path: '/commission/listeCommision' }, { label: 'Détails' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Commission #{id}</h6>
          </div>
          <div className="card-body">
            <div className="row mb-4">
              <div className="col-md-6">
                <table className="table table-bordered">
                  <tbody>
                    <tr><th>Revendeur</th><td>{c.revendeur ? `${c.revendeur.firstName} ${c.revendeur.lastName}` : '-'}</td></tr>
                    <tr><th>Mois/Année</th><td>{c.monthName || '-'}/{c.annee || '-'}</td></tr>
                    <tr><th>Montant HT</th><td>{formatCurrency(c.montantHt)}</td></tr>
                    <tr><th>Montant TTC</th><td>{formatCurrency(c.montantTtc)}</td></tr>
                    <tr><th>Statut</th><td>
                      <span className={`badge badge-${c.statut === 'Payé' ? 'success' : 'warning'}`}>{c.statut || '-'}</span>
                    </td></tr>
                  </tbody>
                </table>
              </div>
            </div>

            {c.abonnements && c.abonnements.length > 0 && (
              <>
                <h5>Abonnements concernés</h5>
                <DataTable
                  columns={columns}
                  data={c.abonnements}
                  emptyMessage="Aucun abonnement"
                />
              </>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default DetailsCommission
