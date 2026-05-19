import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const AllPayements = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/payement/Listepayement', { params: { page: page - 1, size: 20 } })
      .then(res => {
        const d = res.data
        setData(d?.content || d || [])
        setPagination(prev => ({ ...prev, page, total: d?.totalElements || 0, totalPages: d?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'N° Paiement', field: 'numeroPaiement' },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Montant', render: (r) => formatCurrency(r.montant) },
    { header: 'Type', render: (r) => r.typePaiement?.typePaiementName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Validé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'warning'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.datePaiement || r.dateCreation) },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des paiements" breadcrumb={[{ label: 'Paiements' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Paiements</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun paiement trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllPayements
