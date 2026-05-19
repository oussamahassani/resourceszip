import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDateTime } from '../../utils/helpers'

const HistoriqueGlobal = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })
  const [filters, setFilters] = useState({ action: '', utilisateur: '', dateDebut: '', dateFin: '' })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/historique/historique', {
      params: { page: page - 1, size: 20, ...filters }
    })
      .then(res => {
        const d = res.data
        setData(d?.content || d || [])
        setPagination(prev => ({ ...prev, page, total: d?.totalElements || 0, totalPages: d?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleSearch = (e) => {
    e.preventDefault()
    load(1)
  }

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Action', field: 'action' },
    { header: 'Entité', render: (r) => r.entityType || r.entite || '-' },
    { header: 'Description', render: (r) => (
      <span style={{ maxWidth: 300, display: 'block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
        {r.description || '-'}
      </span>
    )},
    { header: 'Utilisateur', render: (r) => r.user || r.utilisateur || '-' },
    { header: 'Adresse IP', field: 'ipAddress' },
    { header: 'Date', render: (r) => formatDateTime(r.dateAction || r.date) },
  ]

  return (
    <section className="content">
      <PageHeader title="Historique global" breadcrumb={[{ label: 'Historique' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Historique des actions</h6>
          </div>
          <div className="card-body border-bottom">
            <form onSubmit={handleSearch} className="mb-3">
              <div className="row">
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Action" value={filters.action}
                    onChange={e => setFilters(f => ({ ...f, action: e.target.value }))} />
                </div>
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Utilisateur" value={filters.utilisateur}
                    onChange={e => setFilters(f => ({ ...f, utilisateur: e.target.value }))} />
                </div>
                <div className="col-md-2">
                  <input type="date" className="form-control form-control-sm" value={filters.dateDebut}
                    onChange={e => setFilters(f => ({ ...f, dateDebut: e.target.value }))} />
                </div>
                <div className="col-md-2">
                  <input type="date" className="form-control form-control-sm" value={filters.dateFin}
                    onChange={e => setFilters(f => ({ ...f, dateFin: e.target.value }))} />
                </div>
                <div className="col-md-2">
                  <button type="submit" className="btn btn-primary btn-sm btn-block">
                    <i className="fas fa-search mr-1"></i> Rechercher
                  </button>
                </div>
              </div>
            </form>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun historique trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default HistoriqueGlobal
