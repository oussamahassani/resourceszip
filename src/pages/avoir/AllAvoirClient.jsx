import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const AllAvoirClient = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/avoir/AllAvoirClient', { params: { page: page - 1, size: 20 } })
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
    { header: 'N° Avoir', field: 'numeroAvoir' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Montant', render: (r) => formatCurrency(r.montant) },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.published ? 'success' : 'secondary'}`}>
        {r.published ? 'Publié' : 'Non publié'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateAvoir || r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <button className="btn btn-info btn-sm" onClick={() => window.open(`/api/avoir/open/${r.avoirId || r.id}`, '_blank')}>
          <i className="fas fa-file-pdf"></i>
        </button>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Factures d'avoir" breadcrumb={[{ label: 'Avoirs' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Factures d&apos;avoir</h6>
            <Link to="/AvoirClient/addNewAvoir" className="btn btn-primary btn-sm">
              <i className="fas fa-plus mr-1"></i> Créer un avoir
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucune facture d'avoir" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllAvoirClient
