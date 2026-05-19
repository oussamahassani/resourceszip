import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatCurrency } from '../../utils/helpers'

const OffreCommissions = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/commission/offreCommissions')
      .then(res => setData(res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Offre', render: (r) => r.offre?.offreName || r.offreName || '-' },
    { header: 'Taux commission (%)', render: (r) => r.tauxCommission || '-' },
    { header: 'Montant fixe', render: (r) => formatCurrency(r.montantFixe) },
    { header: 'Type', field: 'type' },
    { header: 'Actif', render: (r) => (
      <span className={`badge badge-${r.actif ? 'success' : 'danger'}`}>{r.actif ? 'Oui' : 'Non'}</span>
    )},
  ]

  return (
    <section className="content">
      <PageHeader title="Offres commissions" breadcrumb={[{ label: 'Commissions' }, { label: 'Offres' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Offres de commissions</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune offre de commission trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default OffreCommissions
