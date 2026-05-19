import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const AllDemandeCommission = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/commission/allDemandeCommission')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Montant TTC', render: (r) => formatCurrency(r.montantTtc) },
    { header: 'Mois/Année', render: (r) => `${r.monthName || '-'}/${r.annee || '-'}` },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Approuvé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'warning'}`}>
        {r.statut || 'En attente'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/commission/detailsCommission/${r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Demandes de commission" breadcrumb={[{ label: 'Commissions' }, { label: 'Demandes' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Demandes de commission</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune demande trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllDemandeCommission
