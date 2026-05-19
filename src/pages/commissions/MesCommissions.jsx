import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatCurrency } from '../../utils/helpers'

const MesCommissions = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/commission/mesCommission')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Montant HT', render: (r) => formatCurrency(r.montantHt) },
    { header: 'Montant TTC', render: (r) => formatCurrency(r.montantTtc) },
    { header: 'Mois/Année', render: (r) => `${r.monthName || '-'}/${r.annee || '-'}` },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Payé' ? 'success' : 'warning'}`}>{r.statut || '-'}</span>
    )},
  ]

  return (
    <section className="content">
      <PageHeader title="Mes commissions" breadcrumb={[{ label: 'Commissions' }, { label: 'Mes commissions' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Mes commissions</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune commission trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default MesCommissions
