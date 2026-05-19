import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatCurrency } from '../../utils/helpers'

const AllOffres = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/offre/allOffres')
      .then(res => setData(res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Nom de l\'offre', field: 'offreName' },
    { header: 'Prix HT', render: (r) => formatCurrency(r.prixHt) },
    { header: 'Prix TTC', render: (r) => formatCurrency(r.prixTtc) },
    { header: 'Technologie', field: 'technologie' },
    { header: 'Débit', render: (r) => r.debit ? `${r.debit} Mbps` : '-' },
    { header: 'Actif', render: (r) => (
      <span className={`badge badge-${r.actif ? 'success' : 'danger'}`}>{r.actif ? 'Oui' : 'Non'}</span>
    )},
  ]

  return (
    <section className="content">
      <PageHeader title="Offres" breadcrumb={[{ label: 'Services' }, { label: 'Offres' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Offres</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune offre trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllOffres
