import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const MesReclamations = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/reclamation/mes_reclamations')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'N° Réclamation', field: 'numeroReclamation' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Motif', render: (r) => r.motif?.motifName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Résolu' ? 'success' : r.statut === 'En cours' ? 'warning' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/reclamation/view/${r.reclamationId || r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Mes réclamations" breadcrumb={[{ label: 'Réclamations' }, { label: 'Mes réclamations' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Mes réclamations</h6>
            <Link to="/reclamation/addreclamation" className="btn btn-primary btn-sm">
              <i className="fas fa-plus mr-1"></i> Nouvelle réclamation
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune réclamation trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default MesReclamations
