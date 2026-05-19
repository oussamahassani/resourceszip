import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDateTime } from '../../utils/helpers'

const HistoriqueClient = () => {
  const { id } = useParams()
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/historique/historiqueClient/${id}`)
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Action', field: 'action' },
    { header: 'Description', field: 'description' },
    { header: 'Utilisateur', render: (r) => r.user || '-' },
    { header: 'Date', render: (r) => formatDateTime(r.dateAction || r.date) },
  ]

  return (
    <section className="content">
      <PageHeader title="Historique client"
        breadcrumb={[{ label: 'Historique', path: '/historique/historique' }, { label: 'Client' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Historique du client</h6>
            <Link to={`/client/viewclient/${id}`} className="btn btn-info btn-sm">
              <i className="fas fa-eye mr-1"></i> Voir le client
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun historique" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default HistoriqueClient
