import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import abonnementsService from '../../services/abonnementsService'
import { formatDate } from '../../utils/helpers'

const SuiviDemandesTransferees = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    abonnementsService.getAll({ transferred: true })
      .then(res => setData(res?.content || res || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Référence', field: 'reference' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Offre', render: (r) => r.offre?.offreName || '-' },
    { header: 'Transféré le', render: (r) => formatDate(r.dateTransfert || r.dateModification) },
    { header: 'Statut', render: (r) => (
      <span className="badge badge-info">{r.statut || 'Transféré'}</span>
    )},
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
      <PageHeader title="Suivi des demandes transférées"
        breadcrumb={[{ label: 'Abonnements', path: '/demandeabonnement/alldemandesabonnement/1/20' }, { label: 'Transférées' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Demandes transférées</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune demande transférée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default SuiviDemandesTransferees
