import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AllEngagements = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/engagement/allengagements', { params: { page: page - 1, size: 20 } })
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
    { header: 'N° Engagement', field: 'numeroEngagement' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Offre', render: (r) => r.offre?.offreName || '-' },
    { header: 'Durée', render: (r) => r.duree ? `${r.duree} mois` : '-' },
    { header: 'Date début', render: (r) => formatDate(r.dateDebut) },
    { header: 'Date fin', render: (r) => formatDate(r.dateFin) },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Actif' ? 'success' : r.statut === 'Terminé' ? 'secondary' : 'warning'}`}>
        {r.statut || '-'}
      </span>
    )},
  ]

  return (
    <section className="content">
      <PageHeader title="Engagements" breadcrumb={[{ label: 'Engagements' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Liste des engagements</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun engagement trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllEngagements
