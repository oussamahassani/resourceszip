import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import referentielsService from '../../services/referentielsService'

const AllStatuts = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    referentielsService.getStatuts()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Statut', render: (r) => r.statutName || r.name || '-' },
    { header: 'Couleur', render: (r) => r.couleur ? (
      <span style={{ background: r.couleur, color: '#fff', padding: '2px 8px', borderRadius: 4 }}>{r.couleur}</span>
    ) : '-' },
  ]

  return (
    <section className="content">
      <PageHeader title="Statuts" breadcrumb={[{ label: 'Référentiels' }, { label: 'Statuts' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Statuts</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun statut trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllStatuts
