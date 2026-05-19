import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import abonnementsService from '../../services/abonnementsService'
import { formatDate } from '../../utils/helpers'

const AllAbonnementsTraites = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    abonnementsService.getTraites({ page: page - 1, size: 20 })
      .then(res => {
        setData(res?.content || res || [])
        setPagination(prev => ({ ...prev, page, total: res?.totalElements || 0, totalPages: res?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Référence', field: 'reference' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Offre', render: (r) => r.offre?.offreName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Validé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'info'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date traitement', render: (r) => formatDate(r.dateTraitement || r.dateModification) },
    { header: 'Traité par', render: (r) => r.traitePar || '-' },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/demandeabonnement/getdemandeabonnement/${r.demandeId || r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Abonnements traités" breadcrumb={[{ label: 'Abonnements', path: '/demandeabonnement/alldemandesabonnement/1/20' }, { label: 'Traités' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold text-info">Abonnements traités</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun abonnement traité" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllAbonnementsTraites
