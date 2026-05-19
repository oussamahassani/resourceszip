import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AdminModem = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/modem/adminmodem')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'N° Série', field: 'serialNumber' },
    { header: 'Modèle', field: 'modele' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Disponible' ? 'success' : r.statut === 'Affecté' ? 'info' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Zone', render: (r) => r.zone?.zoneName || '-' },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Date', render: (r) => formatDate(r.dateReception || r.dateCreation) },
  ]

  return (
    <section className="content">
      <PageHeader title="Administration Modems" breadcrumb={[{ label: 'Modems' }, { label: 'Admin' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Administration des modems</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun modem trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AdminModem
