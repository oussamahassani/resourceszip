import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const AllFactures = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })
  const [filters, setFilters] = useState({ reference: '', clientNom: '', statut: '' })
  const [showFilters, setShowFilters] = useState(false)

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/facture', { params: { page: page - 1, size: 20, ...filters } })
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
    { header: 'N° Facture', field: 'numeroFacture' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Montant HT', render: (r) => formatCurrency(r.montantHt) },
    { header: 'Montant TTC', render: (r) => formatCurrency(r.montantTtc) },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Payé' ? 'success' : r.statut === 'En retard' ? 'danger' : 'warning'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateFacture || r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <Link to={`/facture/view/${r.factureId || r.id}`} className="btn btn-info btn-sm mr-1">
            <i className="fas fa-eye"></i>
          </Link>
          <Link to={`/facture/open/${r.factureId || r.id}`} className="btn btn-primary btn-sm" target="_blank">
            <i className="fas fa-file-pdf"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Factures" breadcrumb={[{ label: 'Factures' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex align-items-center">
            <button className="btn btn-sm btn-warning" onClick={() => setShowFilters(f => !f)}>
              <i className="fas fa-filter mr-1"></i> Filtre
            </button>
          </div>
          {showFilters && (
            <div className="card-body border-bottom">
              <form onSubmit={handleSearch}>
                <div className="row">
                  <div className="col-md-3">
                    <input className="form-control form-control-sm" placeholder="N° Facture" value={filters.reference}
                      onChange={e => setFilters(f => ({ ...f, reference: e.target.value }))} />
                  </div>
                  <div className="col-md-3">
                    <input className="form-control form-control-sm" placeholder="Nom client" value={filters.clientNom}
                      onChange={e => setFilters(f => ({ ...f, clientNom: e.target.value }))} />
                  </div>
                  <div className="col-md-3">
                    <select className="form-control form-control-sm" value={filters.statut} onChange={e => setFilters(f => ({ ...f, statut: e.target.value }))}>
                      <option value="">Tous statuts</option>
                      <option value="Payé">Payé</option>
                      <option value="En attente">En attente</option>
                      <option value="En retard">En retard</option>
                    </select>
                  </div>
                  <div className="col-md-3">
                    <button type="submit" className="btn btn-success btn-sm btn-block">
                      <i className="fas fa-search mr-1"></i> Rechercher
                    </button>
                  </div>
                </div>
              </form>
            </div>
          )}
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucune facture trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllFactures
