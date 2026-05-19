import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import clientsService from '../../services/clientsService'
import { formatDate } from '../../utils/helpers'

const AllClientsActive = () => {
  const [clients, setClients] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    clientsService.getActive({ page: page - 1, size: 20 })
      .then(data => {
        setClients(data?.content || data || [])
        setPagination(prev => ({ ...prev, page, total: data?.totalElements || 0, totalPages: data?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Code', field: 'codeClient' },
    { header: 'Nom', render: (r) => `${r.firstName || ''} ${r.lastName || ''}` },
    { header: 'Téléphone', field: 'telephone' },
    { header: 'Gouvernorat', render: (r) => r.gouvernorat?.gouvernoratName || '-' },
    { header: 'Date activation', render: (r) => formatDate(r.dateActivation) },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/client/viewclient/${r.clientId || r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Clients actifs" breadcrumb={[{ label: 'Clients', path: '/client/allclients' }, { label: 'Actifs' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold text-success">Clients actifs</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={clients} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun client actif" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllClientsActive
