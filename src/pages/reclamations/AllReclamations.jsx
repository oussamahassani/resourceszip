import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AllReclamations = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/reclamation/allreclamations', { params: { page: page - 1, size: 20 } })
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
    { header: 'N° Réclamation', field: 'numeroReclamation' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Type', render: (r) => r.typeReclamation || r.type || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Résolu' ? 'success' : r.statut === 'En cours' ? 'warning' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Assigné à', render: (r) => r.assignedTo ? `${r.assignedTo.firstName} ${r.assignedTo.lastName}` : '-' },
    { header: 'Date', render: (r) => formatDate(r.dateReclamation || r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <Link to={`/reclamation/view/${r.reclamationId || r.id}`} className="btn btn-info btn-sm mr-1">
            <i className="fas fa-eye"></i>
          </Link>
          <Link to={`/reclamation/edit/${r.reclamationId || r.id}`} className="btn btn-warning btn-sm">
            <i className="fas fa-edit"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des réclamations" breadcrumb={[{ label: 'Réclamations' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Réclamations</h6>
            <Link to="/reclamation/addreclamation" className="btn btn-primary btn-sm">
              <i className="fas fa-plus mr-1"></i> Nouvelle réclamation
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucune réclamation trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllReclamations
