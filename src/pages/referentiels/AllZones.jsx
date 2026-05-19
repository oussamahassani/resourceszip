import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import referentielsService from '../../services/referentielsService'

const AllZones = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    referentielsService.getZones()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Zone', render: (r) => r.zoneName || r.name || '-' },
    { header: 'Description', field: 'description' },
    { header: 'Actif', render: (r) => (
      <span className={`badge badge-${r.actif !== false ? 'success' : 'danger'}`}>{r.actif !== false ? 'Oui' : 'Non'}</span>
    )},
  ]

  return (
    <section className="content">
      <PageHeader title="Zones" breadcrumb={[{ label: 'Référentiels' }, { label: 'Zones' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Zones</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune zone trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllZones
