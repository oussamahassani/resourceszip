import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import abonnementsService from '../../services/abonnementsService'
import { formatDate } from '../../utils/helpers'

const AllAbonnementsNonSignees = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    abonnementsService.getNonSignees({ page: page - 1, size: 20 })
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
    { header: 'CIN', render: (r) => r.client?.cin || '-' },
    { header: 'Offre/Pack', render: (r) => r.offre?.offreName || r.pack?.packName || '-' },
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
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
      <PageHeader title="Abonnements non signés" breadcrumb={[{ label: 'Abonnements', path: '/demandeabonnement/alldemandesabonnement/1/20' }, { label: 'Non signés' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold text-warning">Abonnements non signés</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun abonnement non signé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllAbonnementsNonSignees
