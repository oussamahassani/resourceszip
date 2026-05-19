import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import referentielsService from '../../services/referentielsService'

const AllTypesPaiement = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    referentielsService.getTypesPaiement()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Type de paiement', render: (r) => r.typePaiementName || r.name || '-' },
    { header: 'Description', field: 'description' },
  ]

  return (
    <section className="content">
      <PageHeader title="Types de paiement" breadcrumb={[{ label: 'Référentiels' }, { label: 'Types paiement' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Types de paiement</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun type de paiement trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllTypesPaiement
