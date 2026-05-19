import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AllModems = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/modem/modems', { params: { page: page - 1, size: 20 } })
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
    { header: 'N° Série', field: 'serialNumber' },
    { header: 'Modèle', render: (r) => r.modele?.modeleName || r.modele || '-' },
    { header: 'Marque', render: (r) => r.marque?.marqueName || r.marque || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Disponible' ? 'success' : r.statut === 'Affecté' ? 'info' : r.statut === 'Défectueux' ? 'danger' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Affecté à', render: (r) => r.client ? `${r.client.firstName} ${r.client.lastName}` : '-' },
    { header: 'Zone', render: (r) => r.zone?.zoneName || '-' },
    { header: 'Date', render: (r) => formatDate(r.dateReception || r.dateCreation) },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des modems" breadcrumb={[{ label: 'Modems' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Modems</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun modem trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllModems
