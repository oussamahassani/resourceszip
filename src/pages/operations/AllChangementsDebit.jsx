import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AllChangementsDebit = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/operationAbonnement/alldemandeschangementdebit', { params: { page: page - 1, size: 20 } })
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
    { header: 'Référence', field: 'reference' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Ancien débit', render: (r) => r.ancienDebit ? `${r.ancienDebit} Mbps` : '-' },
    { header: 'Nouveau débit', render: (r) => r.nouveauDebit ? `${r.nouveauDebit} Mbps` : '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Validé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'warning'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
  ]

  return (
    <section className="content">
      <PageHeader title="Demandes de changement de débit" breadcrumb={[{ label: 'Opérations' }, { label: 'Changement débit' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Demandes de changement de débit</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucune demande de changement de débit" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllChangementsDebit
