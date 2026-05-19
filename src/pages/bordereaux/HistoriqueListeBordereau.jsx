import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const HistoriqueListeBordereau = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/bordereau/historique')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'N° Bordereau', field: 'numeroBordereau' },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Montant', render: (r) => formatCurrency(r.montant) },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Validé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date traitement', render: (r) => formatDate(r.dateTraitement) },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/bordereaux/detailsbordereau/${r.bordereauId || r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Historique des bordereaux"
        breadcrumb={[{ label: 'Bordereaux', path: '/bordereaux/Listebordereaux' }, { label: 'Historique' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Historique des bordereaux</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun historique trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default HistoriqueListeBordereau
